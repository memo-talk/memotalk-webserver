package com.memotalk.api.memouser.controller;

import com.memotalk.api.email.dto.AuthCodeRequestDTO;
import com.memotalk.api.memouser.dto.*;
import com.memotalk.api.memouser.service.MemoUserService;
import com.memotalk.util.CookieUtil;
import com.memotalk.util.HeaderUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/memo-user")
@RequiredArgsConstructor
@Tag(name = "Memo User", description = "메모 사용자 API")
@Slf4j
public class MemoUserController {

    private final MemoUserService memoUserService;
    @Value("${jwt.refresh-expiration-in-ms}")
    private int refreshTokenExpirationTime;
    private static final String REFRESH_TOKEN = "refreshToken";

    @Operation(summary = "회원 가입 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원 가입 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식"),
            @ApiResponse(responseCode = "409", description = "중복된 이메일")
    })
    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody MemoUserSignupRequestDTO dto) {
        memoUserService.signup(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "회원 탈퇴 API")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원 탈퇴 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식")
    })
    @PostMapping("/withdraw")
    public ResponseEntity<Void> withdraw(@Parameter(hidden = true) @AuthenticationPrincipal String email) {
        memoUserService.withdraw(email);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "로그인 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = MemoUserSigninResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping("/signin")
    public ResponseEntity<MemoUserSigninResponseDTO> signin(@Valid @RequestBody MemoUserSigninRequestDTO requestDTO, HttpServletResponse response, HttpServletRequest request) {
        MemoUserSigninResponseDTO responseDTO = memoUserService.signin(requestDTO);
        CookieUtil.addSameSiteCookie(response, REFRESH_TOKEN, responseDTO.getRefreshToken(), refreshTokenExpirationTime);

        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @Operation(summary = "내 정보 불러오기 API")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "내 정보 불러오기 성공",
                    content = @Content(schema = @Schema(implementation = MemoUserSigninResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping("/info")
    public ResponseEntity<MemoUserResponseDTO> info(@Parameter(hidden = true) @AuthenticationPrincipal String email) {
        log.info("이메일 : {}", email);
        MemoUserResponseDTO responseDTO = memoUserService.info(email);
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @Operation(summary = "계정 잠금 API")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "계정 잠금 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PatchMapping("/lock")
    public ResponseEntity<Void> lock(@Parameter(hidden = true) @AuthenticationPrincipal String email) {
        memoUserService.lock(email);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "계정 잠금 해제 API")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "계정 잠금 해제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PatchMapping("/unlock")
    public ResponseEntity<Void> unlock(@Parameter(hidden = true) @AuthenticationPrincipal String email, @RequestBody MemoUserUnlockRequestDTO requestDTO) {
        memoUserService.unlock(email, requestDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "비밀번호 재설정 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "계정 잠금 해제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PatchMapping("/password-reset")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody MemoUserPasswordResetRequestDTO requestDTO) {
        memoUserService.resetPassword(requestDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "리프레시 토큰 API")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리프레시 성공",
                    content = @Content(schema = @Schema(implementation = MemoUserSigninResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping("/refresh")
    public ResponseEntity<MemoUserSigninResponseDTO> refreshToken(
            HttpServletRequest request) {

        // access token 확인
        String accessToken = HeaderUtil.getAccessToken(request);
        String refreshToken = CookieUtil.getCookie(request, REFRESH_TOKEN)
                .map(Cookie::getValue)
                .orElse((null));
        MemoUserSigninResponseDTO responseDTO = memoUserService.refresh(refreshToken, accessToken);

        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @Operation(summary = "이메일 유효성 체킹 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 유효성 체킹 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식 or 유효하지 않은 이메일")
    })
    @PostMapping("/validate-email")
    public ResponseEntity<Void> validateEmail(@Valid @RequestBody AuthCodeRequestDTO dto) {
        memoUserService.validateEmailNotExists(dto.getEmail());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "패스워드 유효성 체킹 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "패스워드 유효성 체킹 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식 or 유효하지 않은 패스워드")
    })
    @PostMapping("/validate-password")
    public ResponseEntity<Void> validatePassword(@Parameter(hidden = true) @AuthenticationPrincipal String email,
                                                 @Valid @RequestBody MemoUserUnlockRequestDTO dto) {
        memoUserService.validatePassword(email, dto.getPassword());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
