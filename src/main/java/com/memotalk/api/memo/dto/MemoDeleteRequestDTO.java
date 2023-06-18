package com.memotalk.api.memo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MemoDeleteRequestDTO {

    @NotNull
    private Long workspaceId;

    @NotNull
    private List<Long> memoIdList;
}
