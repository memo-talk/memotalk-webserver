package com.memotalk.api.workspace.controller;

import com.memotalk.api.memo.dto.MemoResponseDTO;
import com.memotalk.api.memo.service.MemoService;
import com.memotalk.api.memouser.dto.MemoGroupByDateDTO;
import com.memotalk.api.workspace.dto.WorkSpaceModifyRequestDTO;
import com.memotalk.api.workspace.dto.WorkSpaceResponseDTO;
import com.memotalk.api.workspace.service.WorkSpaceService;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/workspace")
@RequiredArgsConstructor
@Tag(name = "WorkSpace", description = "워크스페이스 API")
public class WorkSpaceController {

    private final WorkSpaceService workSpaceService;
    private final MemoService memoService;


    @Operation(summary = "워크스페이스 생성 API")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "워크스페이스 생성 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping
    public ResponseEntity<Void> create(@Parameter(hidden = true) @AuthenticationPrincipal String email) {
        workSpaceService.create(email);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "워크스페이스 수정 API")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "워크스페이스 수정 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "워크스페이스를 찾을 수 없음")
    })
    @PatchMapping
    public ResponseEntity<Void> modify(@Parameter(hidden = true) @AuthenticationPrincipal String email, @RequestBody @Valid WorkSpaceModifyRequestDTO requestDTO) {
        workSpaceService.modify(email, requestDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "워크스페이스 삭제 API")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "워크스페이스 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "워크스페이스를 찾을 수 없음")
    })
    @DeleteMapping("/{workspaceId}")
    public ResponseEntity<Void> delete(@Parameter(hidden = true) @AuthenticationPrincipal String email, @PathVariable Long workspaceId) {
        workSpaceService.delete(email, workspaceId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "워크스페이스 상세보기 API")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "워크스페이스 상세보기 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "워크스페이스를 찾을 수 없음")
    })
    @GetMapping("/{workspaceId}")
    public ResponseEntity<WorkSpaceResponseDTO> getWorkspace(@Parameter(hidden = true) @AuthenticationPrincipal String email, @PathVariable Long workspaceId) {
        WorkSpaceResponseDTO workSpaceResponseDTO = workSpaceService.getWorkspace(email, workspaceId);
        return ResponseEntity.status(HttpStatus.OK).body(workSpaceResponseDTO);
    }

    @Operation(summary = "워크스페이스 목록 조회 API")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "워크스페이스 목록 조회 성공",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = WorkSpaceResponseDTO.class)))),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping("/list")
    public ResponseEntity<List<WorkSpaceResponseDTO>> getList(@Parameter(hidden = true) @AuthenticationPrincipal String email) {
        List<WorkSpaceResponseDTO> workSpaceResponseDTOList = workSpaceService.getList(email);
        return ResponseEntity.status(HttpStatus.OK).body(workSpaceResponseDTOList);
    }

    @Operation(summary = "메모 목록 조회 API")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메모 목록 조회 성공",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = MemoResponseDTO.class)))),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "워크스페이스를 찾을 수 없음")
    })
    @GetMapping("/memo-list/{workspaceId}")
    public ResponseEntity<List<MemoGroupByDateDTO>> getMemoList(@PathVariable Long workspaceId) {
        List<MemoGroupByDateDTO> memoResponseDTOList = memoService.getMemoList(workspaceId);
        return ResponseEntity.status(HttpStatus.OK).body(memoResponseDTOList);
    }

    @Operation(summary = "메모 키워드 검색 API")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "메모 목록 조회 성공", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = MemoResponseDTO.class)))), @ApiResponse(responseCode = "401", description = "인증 실패"), @ApiResponse(responseCode = "404", description = "워크스페이스를 찾을 수 없음")})
    @GetMapping("/memo-list/{workspaceId}/{keyword}")
    public ResponseEntity<Slice<MemoResponseDTO>> searchMemoWithKeyword(@PathVariable Long workspaceId, @PathVariable String keyword, @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Slice<MemoResponseDTO> memoResponseDTOList = memoService.searchMemoWithKeyword(workspaceId, keyword, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(memoResponseDTOList);
    }

    @Operation(summary = "워크스페이스 상단 이동 API")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "워크스페이스 상단 이동 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "워크스페이스를 찾을 수 없음")
    })
    @PostMapping("/move-top/{workspaceId}")
    public ResponseEntity<Void> moveTopWorkspace(@Parameter(hidden = true) @AuthenticationPrincipal String email, @PathVariable Long workspaceId) {
        workSpaceService.moveTopWorkspace(email, workspaceId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


}
