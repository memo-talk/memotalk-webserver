package com.memotalk.api.memo.service;

import com.memotalk.api.memo.dto.*;
import com.memotalk.api.memo.entity.Memo;
import com.memotalk.api.memo.respository.MemoRepository;
import com.memotalk.api.memouser.dto.MemoGroupByDateDTO;
import com.memotalk.api.memouser.entity.MemoUser;
import com.memotalk.api.memouser.respository.MemoUserRepository;
import com.memotalk.api.workspace.entity.WorkSpace;
import com.memotalk.api.workspace.respository.WorkSpaceRepository;
import com.memotalk.exception.NotFoundException;
import com.memotalk.exception.enumeration.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MemoService {
    private final MemoRepository memoRepository;
    private final WorkSpaceRepository workSpaceRepository;
    private final FileService fileService;
    private final MemoUserRepository memoUserRepository;
    private static final long INIT_PRIORITY = 9999L;
    private static final long NEXT_ORDER = 1;

    @Transactional(readOnly = true)
    public List<MemoGroupByDateDTO> getMemoList(Long workspaceId) {
        List<Memo> memos = memoRepository.findAllByWorkspace_Id(workspaceId);

        Map<LocalDate, Map<LocalTime, List<Memo>>> groupedMemos = memos.stream()
                .collect(Collectors.groupingBy(memo -> memo.getCreatedAt().toLocalDate(),
                        Collectors.groupingBy(memo -> memo.getCreatedAt().toLocalTime().truncatedTo(ChronoUnit.MINUTES))));

        List<MemoGroupByDateDTO> groupedMemosDTO = new ArrayList<>();
        for (Map.Entry<LocalDate, Map<LocalTime, List<Memo>>> dateEntry : groupedMemos.entrySet()) {
            Map<LocalTime, List<MemoResponseDTO>> timeToMemos = new HashMap<>();
            for (Map.Entry<LocalTime, List<Memo>> timeEntry : dateEntry.getValue().entrySet()) {
                List<MemoResponseDTO> memoDTOs = timeEntry.getValue().stream().map(MemoResponseDTO::new).collect(Collectors.toList());
                timeToMemos.put(timeEntry.getKey(), memoDTOs);
            }
            groupedMemosDTO.add(new MemoGroupByDateDTO(dateEntry.getKey(), timeToMemos));
        }

        return groupedMemosDTO;
    }

    public Memo createMemo(MemoRequestDTO memoRequestDto) {
        WorkSpace workSpace = workSpaceRepository.findById(memoRequestDto.getWorkspaceId()).orElseThrow(
                () -> new NotFoundException(ErrorCode.WORKSPACE_NOT_FOUND)
        );
        return memoRepository.save(new Memo(workSpace, memoRequestDto.getContent(), null));
    }

    public Memo uploadFile(FileUploadRequestDTO fileUploadRequestDTO){
        WorkSpace workSpace = workSpaceRepository.findById(fileUploadRequestDTO.getWorkspaceId()).orElseThrow(
                () -> new NotFoundException(ErrorCode.WORKSPACE_NOT_FOUND)
        );

        String s3FileUrl = fileService.upload(fileUploadRequestDTO.getMultipartFile());
        return memoRepository.save(new Memo(workSpace, null, s3FileUrl));
    }

    public void deleteMemo(List<Long> memoIdList) {

        for (Long memoId : memoIdList){
            Memo memo = memoRepository.findById(memoId)
                    .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_MEMO));
            if (memo.getS3FileUrl() != null){
                fileService.fileDelete(memo.getS3FileUrl());
            }
            memoRepository.delete(memo);
        }
    }

    public Memo markMemoImportant(MemoMarkImportantRequestDTO memoMarkImportantRequestDTO) {
        return memoRepository.findById(memoMarkImportantRequestDTO.getMemoId()).orElseThrow(
                () -> new NotFoundException(ErrorCode.NOT_FOUND_MEMO)
        ).markImportant();
    }

    @Transactional(readOnly = true)
    public List<MemoResponseDTO> searchMemoWithKeyword(Long workspaceId, String keyword) {
        return memoRepository.findAllByWorkspace_IdAndDescriptionContainingOrderByCreatedAtDesc(workspaceId, keyword)
                .stream().map(MemoResponseDTO::new)
                .collect(Collectors.toList());
    }

    public void deleteAllMemo() {
        memoRepository.deleteAll();
    }

    public void convertTodo(ConvertReqeustDTO convertReqeustDTO) {
        Memo memo = memoRepository.findByWorkspace_Id(convertReqeustDTO.getWorkspaceId());

        MemoUser memoUser = memoUserRepository.findByEmail(convertReqeustDTO.getEmail()).orElseThrow(
                () -> new NotFoundException(ErrorCode.USER_NOT_FOUND)
        );

        long priority = workSpaceRepository.findDistinctTopByMemoUser_EmailOrderByPriorityDesc(convertReqeustDTO.getEmail())
                .map(WorkSpace::getPriority)
                .orElse(INIT_PRIORITY);

        workSpaceRepository.save(new WorkSpace(memoUser, priority + NEXT_ORDER, memo.getDescription()));
    }
}
