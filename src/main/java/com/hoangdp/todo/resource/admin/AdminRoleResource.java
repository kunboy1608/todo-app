package com.hoangdp.todo.resource.admin;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hoangdp.todo.entity.Role;
import com.hoangdp.todo.service.RoleService;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminRoleResource {
    private final Logger log = LoggerFactory.getLogger(AdminRoleResource.class);

    @Autowired
    RoleService roleService;

    @GetMapping("/roles")
    public ResponseEntity<List<Role>> findAll() {
        log.debug("REST request to find all role in Admin Mode");        
        return ResponseEntity.ofNullable(roleService.findAll());
    }

    @GetMapping("/roles/{roleId}")
    public ResponseEntity<Role> findById(@PathVariable Long roleId) {
        log.debug("REST request to find role {}", roleId);
        return ResponseEntity.ofNullable(roleService.findById(roleId));
    }

    @PostMapping("/roles")
    public ResponseEntity<Role> create(@RequestBody Role role) {
        log.debug("REST request to create role");
        if (role.getId() != null) {
            throw new ErrorResponseException(HttpStatusCode.valueOf(404));
        }
        return ResponseEntity.status(201).body(roleService.create(role));
    }

    @PutMapping("/roles/{roleId}")
    public ResponseEntity<Role> update(@PathVariable(name = "roleId") Long roleId, @RequestBody Role role) {
        log.debug("REST request to update role {}", roleId);
        if (roleId == role.getId()) {
            return ResponseEntity.ofNullable(roleService.update(role));
        }
        return ResponseEntity.notFound().build();
    }

    // @PatchMapping("/roles/{roleId}")
    // public ResponseEntity<Role> partialUpdate(@PathVariable Long roleId, @RequestBody Map<String, Object> updates) {
    //     log.debug("REST request to partial update role {}", roleId);
    //     if (updates.containsKey("id") && roleId.equals(Long.valueOf(updates.get("id").toString()))) {
    //         return ResponseEntity.ofNullable(roleService.update());
    //     }
    //     return ResponseEntity.ofNullable(null);
    // }

    @DeleteMapping("/roles/{roleId}")
    public ResponseEntity<Void> deleteById(@PathVariable Long roleId) {
        log.debug("REST request to delete role {}", roleId);
        roleService.deleteById(roleId);
        if (roleService.existsById(roleId)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok().build();
    }
}
