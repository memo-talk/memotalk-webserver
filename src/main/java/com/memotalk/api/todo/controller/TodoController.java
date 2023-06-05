package com.memotalk.api.todo.controller;

import com.memotalk.api.todo.dto.TodoRequestDTO;
import com.memotalk.api.todo.dto.TodoResponseDTO;
import com.memotalk.api.todo.service.TodoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "Todo", description = "할 일 API")
@RestController
@RequestMapping("/api/v1/todo")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @Operation(summary = "할 일 생성 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "할 일 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식")
    })
    @PostMapping("/create")
    public ResponseEntity<Void> create(@RequestBody @Valid TodoRequestDTO requestDTO){
        todoService.create(requestDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "할 일 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "할 일 조회 성공"),
            @ApiResponse(responseCode = "404", description = "할 일을 찾을 수 없음")
    })
    @GetMapping("/load/{todoId}")
    public ResponseEntity<TodoResponseDTO> loadTodo(@PathVariable Long todoId){
        TodoResponseDTO todoResponseDTO = todoService.loadTodo(todoId);
        return ResponseEntity.status(HttpStatus.OK).body(todoResponseDTO);
    }

    @Operation(summary = "할 일 삭제 API")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "할 일 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "할 일을 찾을 수 없음")
    })
    @DeleteMapping("/delete/{todoId}")
    public ResponseEntity<Void> delete(@Parameter(hidden = true) @AuthenticationPrincipal String email, @PathVariable Long todoId){
        todoService.delete(email, todoId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "할 일 전체 삭제 API")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "할 일 전체 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "워크 스페이스를 찾을 수 없음")
    })
    @DeleteMapping("/delete-all/{workspaceId}")
    public ResponseEntity<Void> deleteAll(@Parameter(hidden = true) @AuthenticationPrincipal String email, @PathVariable Long workspaceId){
        todoService.deleteAll(email, workspaceId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "할 일 상태 변경 API")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "할 일 상태 변경 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "할 일을 찾을 수 없음")
    })
    @PostMapping("/change-status/{todoId}")
    public ResponseEntity<Void> changeStatus(@Parameter(hidden = true) @AuthenticationPrincipal String email, @PathVariable Long todoId){
        todoService.changeStatus(email, todoId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "할 일 목록 조회 API")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "할 일 목록 조회 성공",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TodoResponseDTO.class)))),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "워크스페이스를 찾을 수 없음")
    })
    @GetMapping("/todo-list/{workspaceId}")
    public ResponseEntity<List<TodoResponseDTO>> getTodoList(@PathVariable Long workspaceId) {
        List<TodoResponseDTO> todoResponseDTOList = todoService.getTodoList(workspaceId);
        return ResponseEntity.status(HttpStatus.OK).body(todoResponseDTOList);
    }

    @Operation(summary = "완료된 할 일 목록 조회 API")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "완료된 할 일 목록 조회 성공",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TodoResponseDTO.class)))),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "워크스페이스를 찾을 수 없음")
    })
    @GetMapping("/todo-list/{workspaceId}/done")
    public ResponseEntity<List<TodoResponseDTO>> getDoneTodoList(@PathVariable Long workspaceId) {
        List<TodoResponseDTO> todoResponseDTOList = todoService.getDoneTodoList(workspaceId);
        return ResponseEntity.status(HttpStatus.OK).body(todoResponseDTOList);
    }
}
