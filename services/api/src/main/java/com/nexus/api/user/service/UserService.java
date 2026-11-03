package com.nexus.api.user.service;

import com.nexus.api.common.exception.ConflictException;
import com.nexus.api.common.exception.NotFoundException;
import com.nexus.api.user.dto.CreateUserRequest;
import com.nexus.api.user.dto.UserResponse;
import com.nexus.api.user.entity.User;
import com.nexus.api.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public UserResponse create(CreateUserRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (repository.existsByEmail(email)) {
            throw new ConflictException("A user with this email already exists");
        }
        String name = request.displayName() == null ? null : request.displayName().trim();
        return UserResponse.from(repository.save(new User(UUID.randomUUID(), email, name)));
    }

    @Transactional(readOnly = true)
    public UserResponse get(UUID id) {
        return repository.findById(id).map(UserResponse::from)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> list() {
        return repository.findAll().stream().map(UserResponse::from).toList();
    }
}
