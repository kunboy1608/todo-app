package com.hoangdp.todo.specification;

import org.springframework.data.jpa.domain.Specification;

import com.hoangdp.todo.entity.ToDo;
import com.hoangdp.todo.entity.ToDo_;
import com.hoangdp.todo.enums.StatusToDoEnum;

public final class ToDoSpecification {
    public static Specification<ToDo> isStatus(StatusToDoEnum status){
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(ToDo_.STATUS), status);
    }
}
