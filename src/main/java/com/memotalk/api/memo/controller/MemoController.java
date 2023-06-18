package com.memotalk.api.memo.controller;

import com.memotalk.api.memo.dto.*;
import com.memotalk.api.memo.service.MemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class MemoController {

    private final SimpMessageSendingOperations messagingTemplate;
    private static final String DESTINATION = "/sub/chat/room/";
    private final MemoService memoService;

    @MessageMapping(value = "/memo")
    public void message(@Valid MemoRequestDTO memoRequestDto) {
        messagingTemplate.convertAndSend(DESTINATION + memoRequestDto.getWorkspaceId(),
                new MemoResponseDTO(memoService.createMemo(memoRequestDto)));
    }

    @MessageMapping(value = "/memo/delete")
    public void deleteMessage(@Valid MemoDeleteRequestDTO memoDeleteRequestDto) {
        memoService.deleteMemo(memoDeleteRequestDto.getMemoIdList());
        messagingTemplate.convertAndSend(DESTINATION + memoDeleteRequestDto.getWorkspaceId()
                , new MemoDeleteResponseDTO("워크스페이스" + memoDeleteRequestDto.getWorkspaceId()
                        + " 의 메모아이디 " + memoDeleteRequestDto.getMemoIdList() + " 에 해당하는 메모가 삭제되었습니다."));
    }

    @MessageMapping(value = "/memo/delete/all")
    public void deleteAllMessage(@Valid MemoDeleteRequestDTO memoDeleteRequestDto) {
        memoService.deleteAllMemo();
        messagingTemplate.convertAndSend(DESTINATION + memoDeleteRequestDto.getWorkspaceId()
                , new MemoDeleteResponseDTO("워크스페이스" + memoDeleteRequestDto.getWorkspaceId()  + " 의 모든 메모가 삭제되었습니다."));
    }

    @MessageMapping(value = "/memo/mark-important")
    public void markMemoImportant(@Valid MemoMarkImportantRequestDTO memoMarkImportantRequestDTO) {
        MemoResponseDTO responseDTO = new MemoResponseDTO(memoService.markMemoImportant(memoMarkImportantRequestDTO));
        messagingTemplate.convertAndSend(DESTINATION + memoMarkImportantRequestDTO.getWorkspaceId(), responseDTO);
    }

    @MessageMapping(value = "/memo/convert-todo")
    public void convertTodo(@Valid ConvertReqeustDTO convertReqeustDTO) {
        MemoResponseDTO responseDTO = new MemoResponseDTO(memoService.markMemoImportant(convertReqeustDTO));
        messagingTemplate.convertAndSend(DESTINATION + memoMarkImportantRequestDTO.getWorkspaceId(), responseDTO);
    }
}
