package com.hoangdp.todo.dto.request;

import java.time.Instant;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class ToDoRequest {

    @NotNull
    private Long id;

    @NotBlank
    private String content;

    @NotNull
    private Instant deadline;
}
