package com.hoangdp.todo.resource;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hoangdp.todo.entity.ToDo;
import com.hoangdp.todo.enums.StatusToDoEnum;
import com.hoangdp.todo.service.ToDoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1")
public class ToDoResource {

    private final Logger log = LoggerFactory.getLogger(ToDoResource.class);

    @Autowired
    ToDoService toDoService;

    @Operation(description = "List all user's todo", responses = {
            @ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = ToDo.class))), responseCode = "200")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực"),
            @ApiResponse(responseCode = "403", description = "Truy cập bị cấm"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy")
    })
    @GetMapping("/todos")
    public ResponseEntity<Page<ToDo>> findAll(@RequestParam(required = false) StatusToDoEnum status,
            Pageable pageable) {
        log.debug("REST request to find all To do");
        pageable = pageable == null || pageable.getPageSize() > 20 ? Pageable.ofSize(20) : pageable;
        if (status == null) {
            return ResponseEntity.ofNullable(toDoService.findAll(pageable));
        } else {
            return ResponseEntity.ofNullable(toDoService.findAllByStatus(status, pageable));
        }
    }

    @GetMapping("/todos/{toDoId}")
    public ResponseEntity<ToDo> findById(@PathVariable Long toDoId) {
        log.debug("REST request to find To do {}", toDoId);
        return ResponseEntity.ofNullable(toDoService.findById(toDoId));
    }

    @PostMapping("/todos")
    public ResponseEntity<ToDo> create(@RequestBody ToDo toDo) {
        log.debug("REST request to create To do");
        if (toDo.getId() != null) {
            throw new ErrorResponseException(HttpStatusCode.valueOf(404));
        }
        return ResponseEntity.status(201).body(toDoService.create(toDo));
    }

    @PutMapping("/todos/{toDoId}")
    public ResponseEntity<ToDo> update(@PathVariable(name = "toDoId") Long toDoId, @RequestBody ToDo toDo) {
        log.debug("REST request to update To do {}", toDoId);
        if (toDoId == toDo.getId()) {
            return ResponseEntity.ofNullable(toDoService.update(toDo));
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/todos/{toDoId}")
    public ResponseEntity<ToDo> partialUpdate(@PathVariable Long toDoId, @RequestBody Map<String, Object> updates) {
        log.debug("REST request to partial update To do {}", toDoId);
        if (updates.containsKey("id") && toDoId.equals(Long.valueOf(updates.get("id").toString()))) {
            return ResponseEntity.ofNullable(toDoService.partialUpdate(updates));
        }
        return ResponseEntity.ofNullable(null);
    }

    @DeleteMapping("/todos/{toDoId}")
    public ResponseEntity<Void> deleteById(@PathVariable Long toDoId) {
        log.debug("REST request to delete To do {}", toDoId);
        toDoService.deleteById(toDoId);
        if (toDoService.existsById(toDoId)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok().build();
    }

}
