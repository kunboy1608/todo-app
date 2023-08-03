package com.hoangdp.todo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hoangdp.todo.entity.Role;
import com.hoangdp.todo.enums.RoleEnum;

public interface RoleRepository extends JpaRepository<Role, Long>{
    Optional<Role> findByName(RoleEnum name);    
}
