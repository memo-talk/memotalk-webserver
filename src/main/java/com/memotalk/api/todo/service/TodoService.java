package com.memotalk.api.todo.service;

import com.memotalk.api.todo.dto.TodoGroupByDateDTO;
import com.memotalk.api.todo.dto.TodoRequestDTO;
import com.memotalk.api.todo.dto.TodoResponseDTO;
import com.memotalk.api.todo.dto.TodoUpdateRequestDTO;
import com.memotalk.api.todo.entity.Todo;
import com.memotalk.api.todo.entity.enumeration.Status;
import com.memotalk.api.todo.respository.TodoRepository;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TodoService {
    private final WorkSpaceRepository workSpaceRepository;
    private final TodoRepository todoRepository;

    @Transactional(readOnly = true)
    public List<TodoGroupByDateDTO> getTodoList(Long workspaceId) {
        List<Todo> todos = todoRepository.findAllByWorkspace_IdAndStatus(workspaceId, Status.TODO);

        TreeMap<LocalDate, TreeMap<LocalTime, List<Todo>>> groupedTodos = todos.stream()
                .collect(Collectors.groupingBy(todo -> todo.getCreatedAt().toLocalDate(),
                                TreeMap::new,
                                Collectors.groupingBy(todo -> todo.getCreatedAt().toLocalTime().truncatedTo(ChronoUnit.MINUTES),
                                        TreeMap::new,
                                        Collectors.collectingAndThen(
                                                Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Todo::getId).reversed())),
                                                ArrayList::new)
                                )
                        )
                );

        List<TodoGroupByDateDTO> groupedTodosDTO = new ArrayList<>();
        for (Map.Entry<LocalDate, TreeMap<LocalTime, List<Todo>>> dateEntry : groupedTodos.descendingMap().entrySet()) {
            Map<LocalTime, List<TodoResponseDTO>> timeToTodos = new LinkedHashMap<>();
            for (Map.Entry<LocalTime, List<Todo>> timeEntry : dateEntry.getValue().entrySet()) {
                List<TodoResponseDTO> todoDTOs = timeEntry.getValue().stream().map(TodoResponseDTO::new).collect(Collectors.toList());
                timeToTodos.put(timeEntry.getKey(), todoDTOs);
            }
            groupedTodosDTO.add(new TodoGroupByDateDTO(dateEntry.getKey(), timeToTodos));
        }
        return groupedTodosDTO;
    }

    public void create(TodoRequestDTO requestDTO) {
        WorkSpace workspace = workSpaceRepository.findById(requestDTO.getWorkspaceId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.WORKSPACE_NOT_FOUND));

        todoRepository.save(new Todo(workspace, requestDTO.getContent()));
    }

    @Transactional(readOnly = true)
    public TodoResponseDTO loadTodo(Long todoId) {
        return new TodoResponseDTO(todoRepository.findById(todoId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.TODO_NOT_FOUND)));
    }

    public void delete(String email, Long todoId) {
        todoRepository.deleteByWorkspace_MemoUser_EmailAndId(email, todoId);
    }

    public void changeStatus(String email, Long todoId) {
        todoRepository.findByWorkspace_MemoUser_EmailAndId(email, todoId).changeStatus();
    }

    public List<TodoResponseDTO> getDoneTodoList(Long workspaceId) {
        return todoRepository.findAllByWorkspace_IdAndStatus(workspaceId, Status.DONE)
                .stream()
                .map(TodoResponseDTO::new)
                .collect(Collectors.toList());
    }

    public void deleteAll(String email, Long workspaceId) {
        todoRepository.deleteAllByWorkspace_MemoUser_EmailAndWorkspace_Id(email, workspaceId);
    }

    public void update(TodoUpdateRequestDTO requestDTO) {
        todoRepository.findByIdAndWorkspace_Id(requestDTO.getTodoId(), requestDTO.getWorkspaceId()).update(requestDTO.getContent());
    }

    public void deleteDoneTodo(Long workspaceId, String email) {
        todoRepository.deleteDoneTodosByUserId(email, workspaceId);
    }
}
