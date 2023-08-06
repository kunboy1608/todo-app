package com.hoangdp.todo.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hoangdp.todo.entity.ToDo;
import com.hoangdp.todo.entity.User;

public interface ToDoRepository extends JpaRepository<ToDo, Long> {

    Page<ToDo> findAllByUser(User user, Pageable pageable);

    void deleteByIdAndUser(Long id, User user);

    Optional<ToDo> findByIdAndUser(Long id, User user);

}
