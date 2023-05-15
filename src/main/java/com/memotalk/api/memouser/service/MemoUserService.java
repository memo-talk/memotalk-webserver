package com.memotalk.api.memouser.service;

import com.memotalk.api.memouser.dto.*;
import com.memotalk.api.workspace.entity.WorkSpace;
import com.memotalk.api.workspace.respository.WorkSpaceRepository;
import com.memotalk.config.jwt.TokenProvider;
import com.memotalk.exception.BadRequestException;
import com.memotalk.exception.NoAuthException;
import com.memotalk.exception.NotFoundException;
import com.memotalk.exception.enumeration.ErrorCode;
import com.memotalk.api.memouser.entity.MemoUser;
import com.memotalk.api.memouser.respository.MemoUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemoUserService {

    private final MemoUserRepository memoUserRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final WorkSpaceRepository workSpaceRepository;

    public void signup(MemoUserSignupRequestDTO dto) {
        validateEmailNotExists(dto.getEmail());
        MemoUser saved = memoUserRepository.save(new MemoUser(dto.getEmail(), passwordEncoder.encode(dto.getPassword())));
        workSpaceRepository.save(new WorkSpace(saved));
    }

    public MemoUserSigninResponseDTO signin(MemoUserSigninRequestDTO requestDTO) {
        MemoUser memoUser = memoUserRepository.findByEmail(requestDTO.getEmail()).orElseThrow(
                () -> new NotFoundException(ErrorCode.USER_NOT_FOUND)
        );

        if (!passwordEncoder.matches(requestDTO.getPassword(), memoUser.getPassword())){
            throw new NoAuthException(ErrorCode.PASSWORD_MISMATCH);
        }

        String accessToken = tokenProvider.generateAccessToken(requestDTO.getEmail());
        return new MemoUserSigninResponseDTO(accessToken);
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
                .orElseThrow(() -> new NoAuthException(ErrorCode.USER_NOT_FOUND_OR_PASSWORD_MISMATCH))
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
}
