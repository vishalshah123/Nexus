package com.nexus.api.user.controller;

import com.nexus.api.user.dto.CreateUserRequest;
import com.nexus.api.user.dto.UserResponse;
import com.nexus.api.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody CreateUserRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<UserResponse> list() {
        return service.list();
    }

    @GetMapping("/{userId}")
    public UserResponse get(@PathVariable UUID userId) {
        return service.get(userId);
    }
}
