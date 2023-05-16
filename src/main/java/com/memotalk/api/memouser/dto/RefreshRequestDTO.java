package com.memotalk.api.memouser.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class RefreshRequestDTO {

    @Schema(description = "리프레시 토큰", required = true)
    @NotNull
    private String refreshToken;

}
