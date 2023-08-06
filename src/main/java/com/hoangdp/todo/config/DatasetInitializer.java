package com.hoangdp.todo.config;

import java.time.Instant;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.hoangdp.todo.entity.Role;
import com.hoangdp.todo.entity.ToDo;
import com.hoangdp.todo.entity.User;
import com.hoangdp.todo.enums.RoleEnum;
import com.hoangdp.todo.repository.RoleRepository;
import com.hoangdp.todo.repository.ToDoRepository;
import com.hoangdp.todo.repository.UserRepository;

import jakarta.transaction.Transactional;

@Configuration
public class DatasetInitializer {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ToDoRepository toDoRepository;

    @Bean
    @Transactional
    public CommandLineRunner commandLineRunner(ApplicationContext context) {
        return args -> {
            roleRepository.save(Role.builder().name(RoleEnum.ADMIN).build());
            roleRepository.save(Role.builder().name(RoleEnum.USER).build());

            userRepository.save(User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin"))
                    .firstName("ADMIN")
                    .lastName("ADMIN")
                    .roles(new HashSet<>() {
                        {
                            add(Role.builder().id(1L).name(RoleEnum.ADMIN).build());
                        }
                    })
                    .build());

            userRepository.save(User.builder()
                    .username("user")
                    .password(passwordEncoder.encode("user"))
                    .firstName("USER")
                    .lastName("USER")
                    .roles(new HashSet<>() {
                        {
                            add(Role.builder().id(2L).name(RoleEnum.USER).build());
                        }
                    })
                    .build());

            toDoRepository.save(ToDo.builder().content("ToDo 1").deadline(Instant.now()).build());
            toDoRepository.save(ToDo.builder().content("ToDo 2").deadline(null).build());
            toDoRepository.save(ToDo.builder().content("ToDo 3").deadline(Instant.now()).build());
            toDoRepository.save(ToDo.builder().content("ToDo 4").deadline(null).build());                                   
        };

    }
}
