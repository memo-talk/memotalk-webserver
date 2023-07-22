package com.memotalk.api.todo.dto;

import com.memotalk.api.todo.entity.Todo;
import com.memotalk.api.todo.entity.enumeration.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TodoResponseDTO {
    @Schema(description = "할일 ID")
    private Long todoId;

    @Schema(description = "할일 상태")
    private Status status;

    @Schema(description = "할일 내용")
    private String description;

    @Schema(description = "워크스페이스 생성일시 (yyyy-MM-dd'T'HH:mm:ss)")
    private String createdAt;

    @Schema(description = "워크스페이스 수정일시 (yyyy-MM-dd'T'HH:mm:ss)")
    private String modifiedAt;

    public TodoResponseDTO(Todo todo) {
        this.todoId = todo.getId();
        this.status = todo.getStatus();
        this.description = todo.getDescription();
        this.createdAt = todo.getCreatedAt().toString();
        this.modifiedAt = todo.getModifiedAt().toString();
    }
}
