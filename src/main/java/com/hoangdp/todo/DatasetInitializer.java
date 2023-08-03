package com.hoangdp.todo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hoangdp.todo.entity.Role;
import com.hoangdp.todo.enums.RoleEnum;
import com.hoangdp.todo.repository.RoleRepository;

@Configuration
public class DatasetInitializer {

    @Autowired
    private RoleRepository roleRepository;

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext context) {
        return args -> {
            roleRepository.save(Role.builder().name(RoleEnum.ADMIN).build());
            roleRepository.save(Role.builder().name(RoleEnum.USER).build());
        };
    }
}
