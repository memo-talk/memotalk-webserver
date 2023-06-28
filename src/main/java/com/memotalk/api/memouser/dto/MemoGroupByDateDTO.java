package com.memotalk.api.memouser.dto;

import com.memotalk.api.memo.dto.MemoResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemoGroupByDateDTO {
    private LocalDate date;
    private Map<LocalTime, List<MemoResponseDTO>> memosByTime;
}
