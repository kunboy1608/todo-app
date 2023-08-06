package com.hoangdp.todo.resource.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminResource {

    @GetMapping("/hello")    
    public ResponseEntity<String> sayHello(){
        return ResponseEntity.ok("Hello, Admin comes back home");
    }
}
