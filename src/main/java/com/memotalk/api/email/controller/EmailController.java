package com.memotalk.api.email.controller;

import com.memotalk.api.email.dto.AuthCodeRequestDTO;
import com.memotalk.api.email.dto.AuthenticationCodeRequestDTO;
import com.memotalk.api.email.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/email")
@RequiredArgsConstructor
@Tag(name = "Email", description = "이메일 인증 관련 API")
public class EmailController {

    private final EmailService emailService;

    @Operation(summary = "이메일 발송 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 발송 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식"),
            @ApiResponse(responseCode = "500", description = "서버 에러 발생")
    })
    @PostMapping("/send")
    public ResponseEntity<Void> sendEmail(@Valid @RequestBody AuthCodeRequestDTO requestDto) {
        emailService.sendEmail(requestDto.getEmail());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "인증코드 확인 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증코드 확인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 에러 발생")
    })
    @PostMapping("/verify")
    public ResponseEntity<Void> verifyAuthCode(@Valid @RequestBody AuthenticationCodeRequestDTO requestDTO) {
        emailService.verifyAuthCode(requestDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
