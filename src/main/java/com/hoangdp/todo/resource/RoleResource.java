package com.hoangdp.todo.resource;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hoangdp.todo.entity.Role;
import com.hoangdp.todo.service.RoleService;

@RestController
@RequestMapping("/api/v1")
public class RoleResource {
    private final Logger log = LoggerFactory.getLogger(RoleResource.class);

    @Autowired
    RoleService roleService;

    @GetMapping("/roles")
    public ResponseEntity<List<Role>> findAll() {
        log.debug("REST request to find all role in User");        
        return ResponseEntity.ofNullable(roleService.findAll());
    }
}
