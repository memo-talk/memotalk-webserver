package com.memotalk.api.workspace.service;

import com.memotalk.api.workspace.dto.WorkSpaceResponseDTO;
import com.memotalk.api.workspace.respository.WorkSpaceRepository;
import com.memotalk.api.memouser.entity.MemoUser;
import com.memotalk.api.memouser.respository.MemoUserRepository;
import com.memotalk.api.workspace.dto.WorkSpaceModifyRequestDTO;
import com.memotalk.api.workspace.entity.WorkSpace;
import com.memotalk.exception.NotFoundException;
import com.memotalk.exception.enumeration.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkSpaceService {

    private final WorkSpaceRepository workSpaceRepository;
    private final MemoUserRepository memoUserRepository;

    public void create(String email) {
        MemoUser memoUser = memoUserRepository.findByEmail(email).orElseThrow(
                () -> new NotFoundException(ErrorCode.USER_NOT_FOUND)
        );
        workSpaceRepository.save(new WorkSpace(memoUser));
    }

    public void modify(String email, WorkSpaceModifyRequestDTO requestDTO) {
        workSpaceRepository.findByIdAndMemoUser_Email(requestDTO.getId(), email).orElseThrow(
                () -> new NotFoundException(ErrorCode.WORKSPACE_NOT_FOUND)
        ).modify(requestDTO.getNewTitle());
    }

    public void delete(String email, Long workspaceId) {
        workSpaceRepository.deleteByIdAndMemoUser_Email(workspaceId, email).orElseThrow(
                () -> new NotFoundException(ErrorCode.WORKSPACE_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<WorkSpaceResponseDTO> getList(String email) {
        return workSpaceRepository.findAllByMemoUser_Email(email).stream()
                .map(WorkSpaceResponseDTO::new)
                .collect(Collectors.toList());
    }

    public void moveTopWorkspace(String email, Long workspaceId){
        Long recentId = workSpaceRepository.findMaxIdByMemoUser_Email(email);
        workSpaceRepository.findById(workspaceId).orElseThrow(
                () -> new NotFoundException(ErrorCode.WORKSPACE_NOT_FOUND)
        ).moveTop(recentId);
    }

    @Transactional(readOnly = true)
    public WorkSpaceResponseDTO getWorkspace(String email, Long workspaceId) {
        WorkSpace workSpace = workSpaceRepository.findByIdAndMemoUser_Email(workspaceId, email).orElseThrow(
                () -> new NotFoundException(ErrorCode.WORKSPACE_NOT_FOUND));

        return new WorkSpaceResponseDTO(workSpace);
    }
}
