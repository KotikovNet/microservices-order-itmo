package com.example.users.controller;

import com.example.users.dto.*;
import com.example.users.service.UserService;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest r) {
        UserResponse u = service.create(r);
        return ResponseEntity.created(URI.create("/api/users/" + u.id())).body(u);
    }

    @GetMapping("/{id}")
    public UserResponse get(@PathVariable long id) {
        return service.get(id);
    }

    @GetMapping
    public List<UserResponse> list() {
        return service.list();
    }

    @PutMapping("/{id}")
    public UserResponse update(@PathVariable long id, @Valid @RequestBody UserRequest r) {
        return service.update(id, r);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Void> exists(@PathVariable long id) {
        return service.exists(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
