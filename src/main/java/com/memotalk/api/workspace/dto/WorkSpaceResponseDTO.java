package com.memotalk.api.workspace.dto;

import com.memotalk.api.workspace.entity.WorkSpace;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WorkSpaceResponseDTO {

    @Schema(description = "워크스페이스 ID")
    private Long id;

    @Schema(description = "워크스페이스 제목")
    private String title;

    @Schema(description = "워크스페이스 이미지 아이디")
    private Integer imageId;

    @Schema(description = "워크스페이스 생성일시 (yyyy-MM-dd'T'HH:mm:ss)")
    private String createdAt;

    @Schema(description = "워크스페이스 수정일시 (yyyy-MM-dd'T'HH:mm:ss)")
    private String modifiedAt;

    public WorkSpaceResponseDTO(WorkSpace workSpace) {
        this.id = workSpace.getId();
        this.title = workSpace.getTitle();
        this.imageId = workSpace.getImageId();
        this.createdAt = workSpace.getCreatedAt().toString();
        this.modifiedAt = workSpace.getModifiedAt().toString();
    }
}
