package com.example.users.service;

import com.example.users.dto.*;
import com.example.users.entity.User;
import com.example.users.exception.ApiException;
import com.example.users.repository.UserRepository;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {
    private final UserRepository users;

    public UserService(UserRepository users) {
        this.users = users;
    }

    public UserResponse create(UserRequest r) {
        if (users.existsByEmail(r.email())) throw ApiException.conflict();
        return UserResponse.from(users.save(new User(r.name(), r.email())));
    }

    @Transactional(readOnly = true)
    public UserResponse get(long id) {
        return UserResponse.from(find(id));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> list() {
        return users.findAll().stream().map(UserResponse::from).toList();
    }

    public UserResponse update(long id, UserRequest r) {
        User u = find(id);
        if (!u.getEmail().equals(r.email()) && users.existsByEmail(r.email())) throw ApiException.conflict();
        u.update(r.name(), r.email());
        return UserResponse.from(users.save(u));
    }

    public void delete(long id) {
        users.delete(find(id));
    }

    @Transactional(readOnly = true)
    public boolean exists(long id) {
        return users.existsById(id);
    }

    private User find(long id) {
        return users.findById(id).orElseThrow(ApiException::notFound);
    }
}
