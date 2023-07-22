package com.memotalk.api.memo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ConvertRequestDTO {
    @NotNull
    private Long memoId;
    @NotNull
    private String email;
}
