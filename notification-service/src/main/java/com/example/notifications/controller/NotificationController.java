package com.example.notifications.controller;

import com.example.notifications.dto.*;
import com.example.notifications.service.NotificationService;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<NotificationResponse> create(@Valid @RequestBody NotificationRequest r) {
        NotificationResponse n = service.create(r);
        return ResponseEntity.created(URI.create("/api/notifications/" + n.id())).body(n);
    }

    @GetMapping("/{id}")
    public NotificationResponse get(@PathVariable long id) {
        return service.get(id);
    }

    @GetMapping
    public List<NotificationResponse> list() {
        return service.list();
    }

    @GetMapping("/user/{userId}")
    public List<NotificationResponse> byUser(@PathVariable long userId) {
        return service.byUser(userId);
    }
}
