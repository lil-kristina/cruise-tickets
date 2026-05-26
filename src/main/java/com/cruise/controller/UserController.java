package com.cruise.controller;

import com.cruise.dto.user.CreateUserRequest;
import com.cruise.dto.user.UserResponse;
import com.cruise.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // GET /api/users — все пользователи (ADMIN, OWNER)
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','OWNER')")
    public ResponseEntity<List<UserResponse>> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }

    // GET /api/users/me — текущий пользователь (любой авторизованный)
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(Authentication auth) {
        return ResponseEntity.ok(userService.getByEmail(auth.getName()));
    }

    // GET /api/users/:id — пользователь по id (ADMIN, OWNER)
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','OWNER')")
    public ResponseEntity<UserResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    // POST /api/users — создать пользователя (OWNER)
    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(userService.create(request));
    }

    // DELETE /api/users/:id — удалить пользователя (OWNER)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}