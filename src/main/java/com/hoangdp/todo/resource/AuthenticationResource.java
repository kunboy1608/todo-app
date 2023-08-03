package com.hoangdp.todo.resource;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hoangdp.todo.dto.request.SignInRequest;
import com.hoangdp.todo.dto.request.SignUpRequest;
import com.hoangdp.todo.dto.response.JwtAuthenticationResponse;
import com.hoangdp.todo.security.AuthenticationService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationResource {

    @Autowired
    private AuthenticationService service;

    @PostMapping("signin")
    public ResponseEntity<JwtAuthenticationResponse> signIn(@Valid @RequestBody SignInRequest request){
        return ResponseEntity.ok(service.signIn(request));
    }

    @PostMapping("signup")
    public ResponseEntity<JwtAuthenticationResponse> signUp(@Valid @RequestBody SignUpRequest request){
        return ResponseEntity.ok(service.signUp(request));
    }
}
