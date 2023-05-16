package com.memotalk.api.memouser.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemoUserSigninResponseDTO {

    @Schema(description = "인증에 사용되는 액세스 토큰")
    private String accessToken;

    @Schema(description = "액세스 토큰 갱신을 위한 리프레시 토큰")
    private String refreshToken;

    public MemoUserSigninResponseDTO(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}