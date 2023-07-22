package com.memotalk.api.workspace.service;

import com.memotalk.api.memouser.entity.MemoUser;
import com.memotalk.api.memouser.respository.MemoUserRepository;
import com.memotalk.api.workspace.dto.WorkSpaceModifyRequestDTO;
import com.memotalk.api.workspace.dto.WorkSpaceResponseDTO;
import com.memotalk.api.workspace.entity.WorkSpace;
import com.memotalk.api.workspace.respository.WorkSpaceRepository;
import com.memotalk.exception.NoAuthException;
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
    private static final long INIT_PRIORITY = 9999L;
    private static final long NEXT_ORDER = 1;

    public void create(String email) {
        if (!memoUserRepository.existsByEmail(email)) {
            throw new NoAuthException(ErrorCode.INVALID_ACCESS_TOKEN);
        }

        MemoUser memoUser = memoUserRepository.findByEmail(email).orElseThrow(
                () -> new NotFoundException(ErrorCode.USER_NOT_FOUND)
        );

        long priority = workSpaceRepository.findDistinctTopByMemoUser_EmailOrderByPriorityDesc(email)
                .map(WorkSpace::getPriority)
                .orElse(INIT_PRIORITY);

        workSpaceRepository.save(new WorkSpace(memoUser, priority + NEXT_ORDER));
    }

    public void modify(String email, WorkSpaceModifyRequestDTO requestDTO) {
        if (!memoUserRepository.existsByEmail(email)) {
            throw new NoAuthException(ErrorCode.INVALID_ACCESS_TOKEN);
        }

        workSpaceRepository.findByIdAndMemoUser_Email(requestDTO.getId(), email).orElseThrow(
                () -> new NotFoundException(ErrorCode.WORKSPACE_NOT_FOUND)
        ).modify(requestDTO.getNewTitle());
    }

    public void delete(String email, Long workspaceId) {
        if (!memoUserRepository.existsByEmail(email)) {
            throw new NoAuthException(ErrorCode.INVALID_ACCESS_TOKEN);
        }

        workSpaceRepository.deleteByIdAndMemoUser_Email(workspaceId, email).orElseThrow(
                () -> new NotFoundException(ErrorCode.WORKSPACE_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<WorkSpaceResponseDTO> getList(String email) {
        if (!memoUserRepository.existsByEmail(email)) {
            throw new NoAuthException(ErrorCode.INVALID_ACCESS_TOKEN);
        }

        return workSpaceRepository.findAllByMemoUser_EmailOrderByPriorityAsc(email).stream()
                .map(WorkSpaceResponseDTO::new)
                .collect(Collectors.toList());
    }

    public void moveTopWorkspace(String email, Long workspaceId) {
        if (!memoUserRepository.existsByEmail(email)) {
            throw new NoAuthException(ErrorCode.INVALID_ACCESS_TOKEN);
        }

        long priority = workSpaceRepository.findDistinctTopByMemoUser_EmailOrderByPriorityAsc(email)
                .map(WorkSpace::getPriority)
                .orElse(INIT_PRIORITY);

        workSpaceRepository.findById(workspaceId).orElseThrow(
                () -> new NotFoundException(ErrorCode.WORKSPACE_NOT_FOUND)
        ).moveTop(priority);
    }

    @Transactional(readOnly = true)
    public WorkSpaceResponseDTO getWorkspace(String email, Long workspaceId) {
        if (!memoUserRepository.existsByEmail(email)) {
            throw new NoAuthException(ErrorCode.INVALID_ACCESS_TOKEN);
        }

        WorkSpace workSpace = workSpaceRepository.findByIdAndMemoUser_Email(workspaceId, email).orElseThrow(
                () -> new NotFoundException(ErrorCode.WORKSPACE_NOT_FOUND));

        return new WorkSpaceResponseDTO(workSpace);
    }
}
