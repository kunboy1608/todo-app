package com.hoangdp.todo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.hoangdp.todo.entity.Role;
import com.hoangdp.todo.enums.RoleEnum;
import com.hoangdp.todo.repository.RoleRepository;

@SpringBootApplication
public class TodoApplication {

	@Autowired
	private RoleRepository roleRepository;
	

	public static void main(String[] args) {
		SpringApplication.run(TodoApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext context){
		return args ->{
			roleRepository.save(Role.builder().name(RoleEnum.ADMIN).build());
			roleRepository.save(Role.builder().name(RoleEnum.USER).build());
		};
	}

}
