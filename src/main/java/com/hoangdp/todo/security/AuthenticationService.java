package com.hoangdp.todo.security;

import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hoangdp.todo.dto.request.SignInRequest;
import com.hoangdp.todo.dto.request.SignUpRequest;
import com.hoangdp.todo.dto.response.JwtAuthenticationResponse;
import com.hoangdp.todo.entity.User;
import com.hoangdp.todo.enums.RoleEnum;
import com.hoangdp.todo.repository.RoleRepository;
import com.hoangdp.todo.repository.UserRepository;

@Service
public class AuthenticationService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public JwtAuthenticationResponse signUp(SignUpRequest signUpRequest){
        User user = User.builder()
                .username(signUpRequest.getUsername())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .firstName(signUpRequest.getFirstName())
                .lastName(signUpRequest.getLastName())
                .roles(new HashSet<>() {
                    {
                        add(roleRepository.findByName(RoleEnum.USER).get());
                    }
                })
                .build();        
        userRepository.save(user);
        return new JwtAuthenticationResponse(jwtService.generateToken(user));
    }

    public JwtAuthenticationResponse signIn(SignInRequest signInRequest){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getUsername(), signInRequest.getPassword()));
        User user = userRepository.findUserByUsername(signInRequest.getUsername()).orElseThrow(() -> new IllegalArgumentException("Invalid user"));        
        return new JwtAuthenticationResponse(jwtService.generateToken(user));
    }
}
