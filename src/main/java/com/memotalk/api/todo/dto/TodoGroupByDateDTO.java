package com.memotalk.api.todo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class TodoGroupByDateDTO {
    private LocalDate date;
    private Map<LocalTime, List<TodoResponseDTO>> todosByTime;

    public TodoGroupByDateDTO(LocalDate date, Map<LocalTime, List<TodoResponseDTO>> todosByTime) {
        this.date = date;
        this.todosByTime = todosByTime;
    }
}