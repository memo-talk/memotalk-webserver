package com.memotalk.api.memo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class MemoDeleteResponseDTO {
    String message;

    public MemoDeleteResponseDTO(String message) {
        this.message = message;
    }
}
