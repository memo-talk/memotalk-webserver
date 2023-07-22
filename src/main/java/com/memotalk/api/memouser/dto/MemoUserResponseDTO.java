package com.memotalk.api.memouser.dto;

import com.memotalk.api.memouser.entity.MemoUser;
import com.memotalk.api.memouser.entity.enumeration.Lock;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemoUserResponseDTO {

    @Schema(description = "로그인된 유저의 이메일")
    private String email;

    @Schema(description = "잠금 상태 ex) 잠금 해제, 잠금 모드")
    private Lock lock;

    public MemoUserResponseDTO(MemoUser memoUser) {
        this.email = memoUser.getEmail();
        this.lock = memoUser.getLock();
    }
}
