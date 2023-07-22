package com.memotalk.api.memo.service;

import com.memotalk.api.memo.dto.*;
import com.memotalk.api.memo.entity.Memo;
import com.memotalk.api.memo.respository.MemoRepository;
import com.memotalk.api.memouser.dto.MemoGroupByDateDTO;
import com.memotalk.api.memouser.respository.MemoUserRepository;
import com.memotalk.api.todo.entity.Todo;
import com.memotalk.api.todo.respository.TodoRepository;
import com.memotalk.api.workspace.entity.WorkSpace;
import com.memotalk.api.workspace.respository.WorkSpaceRepository;
import com.memotalk.exception.NotFoundException;
import com.memotalk.exception.enumeration.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MemoService {
    private final MemoRepository memoRepository;
    private final WorkSpaceRepository workSpaceRepository;
    private final FileService fileService;
    private final MemoUserRepository memoUserRepository;
    private final TodoRepository todoRepository;
    private static final long INIT_PRIORITY = 9999L;
    private static final long NEXT_ORDER = 1;


    @Transactional(readOnly = true)
    public List<MemoGroupByDateDTO> getMemoList(Long workspaceId) {
        List<Memo> memos = memoRepository.findAllByWorkspace_Id(workspaceId);

        TreeMap<LocalDate, TreeMap<LocalTime, List<Memo>>> groupedMemos = memos.stream()
                .collect(Collectors.groupingBy(memo -> memo.getCreatedAt().toLocalDate(),
                                TreeMap::new,
                                Collectors.groupingBy(memo -> memo.getCreatedAt().toLocalTime().truncatedTo(ChronoUnit.MINUTES),
                                        TreeMap::new,
                                        Collectors.collectingAndThen(
                                                Collectors.toCollection(() ->
                                                        new TreeSet<>(Comparator.comparing(Memo::getId).reversed())),
                                                ArrayList::new)
                                )
                        )
                );

        List<MemoGroupByDateDTO> groupedMemosDTO = new ArrayList<>();
        for (Map.Entry<LocalDate, TreeMap<LocalTime, List<Memo>>> dateEntry : groupedMemos.descendingMap().entrySet()) {
            Map<LocalTime, List<MemoResponseDTO>> timeToMemos = new LinkedHashMap<>();
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

    public Memo uploadFile(FileUploadRequestDTO fileUploadRequestDTO) {
        WorkSpace workSpace = workSpaceRepository.findById(fileUploadRequestDTO.getWorkspaceId()).orElseThrow(
                () -> new NotFoundException(ErrorCode.WORKSPACE_NOT_FOUND)
        );

        String s3FileUrl = fileService.upload(fileUploadRequestDTO.getMultipartFile());
        return memoRepository.save(new Memo(workSpace, null, s3FileUrl));
    }

    public void deleteMemo(List<Long> memoIdList) {

        for (Long memoId : memoIdList) {
            Memo memo = memoRepository.findById(memoId)
                    .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_MEMO));
            if (memo.getS3FileUrl() != null) {
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
    public Slice<MemoResponseDTO> searchMemoWithKeyword(Long workspaceId, String keyword, Pageable pageable) {
        return memoRepository.findAllByWorkspace_IdAndDescriptionContainingOrderByCreatedAtDesc(workspaceId, keyword, pageable)
                .map(MemoResponseDTO::new);
    }

    public void deleteAllMemo(Long workspaceId) {
        memoRepository.deleteAllByWorkspace_Id(workspaceId);
    }

    public void convertTodo(ConvertRequestDTO convertRequestDTO) {
        Memo memo = memoRepository.findById(convertRequestDTO.getMemoId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_MEMO));

        todoRepository.save(new Todo(memo.getWorkspace(), memo.getDescription()));
    }
}
