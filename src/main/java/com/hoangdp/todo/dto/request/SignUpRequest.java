package com.hoangdp.todo.dto.request;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class SignUpRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;
}
