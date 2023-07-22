package com.memotalk.api.memo.dto;

import com.memotalk.api.memo.entity.Memo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class MemoGroupDTO {

    private LocalDateTime date;
    private List<Memo> memoList;

    public MemoGroupDTO(LocalDateTime date, List<Memo> memos) {
        this.date = date;
        this.memoList = memos;
    }
}