package com.memotalk.api.memo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Data
@Getter
@NoArgsConstructor
@Schema(description = "파일 업로드 요청 DTO")
public class FileUploadRequestDTO {
    @NotNull
    @Schema(description = "워크스페이스 ID", example = "1")
    private Long workspaceId;
    @NotNull
    @Schema(description = "워크스페이스 ID", example = "스웨거 멀티파트 형식 지원 X")
    private MultipartFile multipartFile;
}
