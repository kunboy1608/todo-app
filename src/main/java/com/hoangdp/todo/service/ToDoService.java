package com.hoangdp.todo.service;

import java.time.Instant;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.hoangdp.todo.entity.ToDo;
import com.hoangdp.todo.entity.User;
import com.hoangdp.todo.repository.ToDoRepository;
import com.hoangdp.todo.utils.TimeUtils;

@Service
public class ToDoService {
    @Autowired
    ToDoRepository toDoRepository;

    @Autowired
    UserService userService;

    public ToDo create(ToDo toDo) {
        final User u = userService.getCurrentUser();

        toDo.setCreatedOn(Instant.now());
        toDo.setCreatedBy(u.getId());
        toDo.setLastModifiedOn(Instant.now());
        toDo.setLastModifiedBy(u.getId());
        toDo.setUser(u);

        return toDoRepository.save(toDo);
    }

    public ToDo update(ToDo toDo) {
        final ToDo t = toDoRepository.findById(toDo.getId()).orElse(null);
        if (t == null) {
            return null;
        }

        final User u = userService.getCurrentUser();

        toDo.setCreatedOn(t.getCreatedOn());
        toDo.setCreatedBy(t.getCreatedBy());
        toDo.setLastModifiedOn(Instant.now());
        toDo.setLastModifiedBy(u.getId());

        return toDoRepository.save(toDo);
    }

    public ToDo partialUpdate(Map<String, Object> updates) {
        ToDo t = toDoRepository.findById(Long.valueOf(updates.get("id").toString())).orElse(null);
        if (t == null) {
            return null;
        }
        if (updates.containsKey("content")) {
            t.setContent(
                    updates.get("content") == null ? null : updates.get("content").toString());
        }
        if (updates.containsKey("deadline")) {
            t.setDeadline(
                    updates.get("deadline") == null ? null
                            : TimeUtils.instantFromString(updates.get("deadline").toString()));
        }

        final User u = userService.getCurrentUser();

        t.setLastModifiedOn(Instant.now());
        t.setLastModifiedBy(u.getId());

        return toDoRepository.save(t);
    }

    public Page<ToDo> findAll(Pageable pageable) {
        return toDoRepository.findAllByUser(userService.getCurrentUser(), pageable);
    }

    public ToDo findById(Long id) {
        return toDoRepository.findByIdAndUser(id, userService.getCurrentUser()).orElse(null);
    }

    public void deleteById(Long id) {
        toDoRepository.deleteByIdAndUser(id, userService.getCurrentUser());
    }

    public boolean existsById(Long id){
        return toDoRepository.existsById(id);
    }
}
