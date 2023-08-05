package com.hoangdp.todo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hoangdp.todo.entity.ToDo;

public interface ToDoRepository extends JpaRepository<ToDo, Long>{
    Page<ToDo> findAll(Pageable pageable);
}
