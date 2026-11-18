package com.example.orders.controller;

import com.example.orders.dto.*;
import com.example.orders.service.OrderService;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody OrderRequest r) {
        OrderResponse o = service.create(r);
        return ResponseEntity.created(URI.create("/api/orders/" + o.id())).body(o);
    }

    @GetMapping("/{id}")
    public OrderResponse get(@PathVariable long id) {
        return service.get(id);
    }

    @GetMapping
    public List<OrderResponse> list() {
        return service.list();
    }

    @GetMapping("/user/{userId}")
    public List<OrderResponse> byUser(@PathVariable long userId) {
        return service.byUser(userId);
    }

    @PutMapping("/{id}")
    public OrderResponse update(@PathVariable long id, @Valid @RequestBody OrderRequest r) {
        return service.update(id, r);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
