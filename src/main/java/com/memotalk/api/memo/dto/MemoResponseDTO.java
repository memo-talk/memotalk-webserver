package com.memotalk.api.memo.dto;

import com.memotalk.api.memo.entity.Memo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MemoResponseDTO {
    private Long id;
    private String content;
    private Boolean isImportant;
    private String s3FileUrl;
    private String createdAt;
    private String modifiedAt;

    public MemoResponseDTO(Memo memo) {
        this.id = memo.getId();
        this.content = memo.getDescription();
        this.createdAt = memo.getCreatedAt().toString();
        this.modifiedAt = memo.getModifiedAt().toString();
        this.isImportant = memo.isImportant();
        this.s3FileUrl = memo.getS3FileUrl();
    }
}