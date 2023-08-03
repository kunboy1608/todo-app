package com.hoangdp.todo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hoangdp.todo.entity.User;

public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findUserByUsername(String username);
}
