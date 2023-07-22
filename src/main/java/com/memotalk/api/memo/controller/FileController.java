package com.memotalk.api.memo.controller;

import com.memotalk.api.memo.dto.FileUploadRequestDTO;
import com.memotalk.api.memo.dto.MemoResponseDTO;
import com.memotalk.api.memo.entity.Memo;
import com.memotalk.api.memo.service.MemoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/file")
@RequiredArgsConstructor
@Tag(name = "File", description = "파일 관련 API")
public class FileController {

    private final MemoService memoService;
    private final SimpMessageSendingOperations messagingTemplate;
    private static final String DESTINATION = "/sub/chat/room/";

    @Operation(summary = "파일 업로드 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "파일 업로드 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식")
    })
    @PostMapping("/memo-upload")
    public ResponseEntity<Void> uploadFile(@Valid @ModelAttribute FileUploadRequestDTO dto) {
        Memo memo = memoService.uploadFile(dto);
        messagingTemplate.convertAndSend(DESTINATION + memo.getWorkspace().getId(),
                new MemoResponseDTO(memo));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
