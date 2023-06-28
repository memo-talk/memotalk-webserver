package com.memotalk.api.memouser.service;

import com.memotalk.api.memo.entity.Memo;
import com.memotalk.api.memouser.dto.*;
import com.memotalk.api.memouser.entity.UserRefreshToken;
import com.memotalk.api.memouser.respository.UserRefreshTokenRepository;
import com.memotalk.api.workspace.entity.WorkSpace;
import com.memotalk.api.workspace.respository.WorkSpaceRepository;
import com.memotalk.config.jwt.TokenProvider;
import com.memotalk.exception.BadRequestException;
import com.memotalk.exception.NotFoundException;
import com.memotalk.exception.enumeration.ErrorCode;
import com.memotalk.api.memouser.entity.MemoUser;
import com.memotalk.api.memouser.respository.MemoUserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Transactional
public class MemoUserService {

    private static final long THREE_DAYS_MSEC = 259200000;
    private static final long INIT_PRIORITY = 10000L;
    private final MemoUserRepository memoUserRepository;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final WorkSpaceRepository workSpaceRepository;

    public void signup(MemoUserSignupRequestDTO dto) {
        validateEmailNotExists(dto.getEmail());
        MemoUser saved = memoUserRepository.save(new MemoUser(dto.getEmail(), passwordEncoder.encode(dto.getPassword())));
        workSpaceRepository.save(new WorkSpace(saved, INIT_PRIORITY));
    }

    public MemoUserSigninResponseDTO signin(MemoUserSigninRequestDTO requestDTO) {
        MemoUser memoUser = memoUserRepository.findByEmail(requestDTO.getEmail()).orElseThrow(
                () -> new NotFoundException(ErrorCode.USER_NOT_FOUND)
        );

        if (!passwordEncoder.matches(requestDTO.getPassword(), memoUser.getPassword())){
            throw new NotFoundException(ErrorCode.PASSWORD_MISMATCH);
        }

        // 토큰 생성
        String accessToken = tokenProvider.generateAccessToken(requestDTO.getEmail());
        String refreshToken = tokenProvider.generateRefreshToken("" + memoUser.getId());

        UserRefreshToken userRefreshToken = userRefreshTokenRepository.findByUserId(memoUser.getId());
        if (userRefreshToken == null) {
            // 없는 경우 새로 등록
            userRefreshToken = new UserRefreshToken(memoUser.getId(), refreshToken);
            userRefreshTokenRepository.saveAndFlush(userRefreshToken);
        } else {
            // DB에 refresh 토큰 업데이트
            userRefreshToken.setRefreshToken(refreshToken);
            userRefreshTokenRepository.saveAndFlush(userRefreshToken);
        }

        return new MemoUserSigninResponseDTO(accessToken, refreshToken);
    }

    public void validateEmail(String email){
        if(!memoUserRepository.existsByEmail(email)){
            throw new BadRequestException(ErrorCode.NOT_FOUND_EMAIL);
        }
    }

    private void validateEmailNotExists(String email){
        if(memoUserRepository.existsByEmail(email)){
            throw new BadRequestException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
    }

    public void lock(String email) {
        memoUserRepository.findByEmail(email).orElseThrow(
                () -> new NotFoundException(ErrorCode.USER_NOT_FOUND)
        ).lock();
    }

    public void unlock(String email, MemoUserUnlockRequestDTO requestDTO) {
        memoUserRepository.findByEmail(email)
                .filter(u -> passwordEncoder.matches(requestDTO.getPassword(), u.getPassword()))
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND_OR_PASSWORD_MISMATCH))
                .unlock();
    }

    public void resetPassword(MemoUserPasswordResetRequestDTO requestDTO) {
        memoUserRepository.findByEmail(requestDTO.getEmail())
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND))
                .resetPassword(passwordEncoder.encode(requestDTO.getPassword()));
    }

    @Transactional(readOnly = true)
    public MemoUserResponseDTO info(String email) {
        return memoUserRepository.findByEmail(email)
                .map(MemoUserResponseDTO::new)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    public MemoUserSigninResponseDTO refresh(String refreshToken, String accessToken, String email) {

        if (!tokenProvider.validate(accessToken)) {
            throw new NotFoundException(ErrorCode.INVALID_ACCESS_TOKEN);
        }
        // expired access token 인지 확인
        Claims claims = tokenProvider.getExpiredTokenClaims(accessToken);
        if (claims == null) {
            throw new NotFoundException(ErrorCode.NOT_EXPIRED_TOKEN_YET);
        }

        if (!tokenProvider.validateToken(refreshToken)) {
            throw new NotFoundException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        Long userId = memoUserRepository.findByEmail(email).orElseThrow(
                () -> new NotFoundException(ErrorCode.USER_NOT_FOUND)
        ).getId();

        // userId refresh token 으로 DB 확인
        UserRefreshToken userRefreshToken = userRefreshTokenRepository.findByUserIdAndRefreshToken(userId, refreshToken);
        if (userRefreshToken == null) {
            throw new NotFoundException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String newAccessToken = tokenProvider.generateAccessToken(email);

        // refresh 토큰 기간이 3일 이하로 남은 경우, refresh 토큰 갱신
        long validTime = tokenProvider.getTokenClaims(refreshToken).getExpiration().getTime() - new Date().getTime();

        if (validTime <= THREE_DAYS_MSEC) {
            // refresh 토큰 설정
            refreshToken = tokenProvider.generateRefreshToken("" + userId);

            // DB에 refresh 토큰 업데이트
            userRefreshToken.setRefreshToken(refreshToken);
        }

        return new MemoUserSigninResponseDTO(newAccessToken, refreshToken);
    }
}
