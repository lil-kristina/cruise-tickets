package com.cruise.service;

import com.cruise.dto.user.CreateUserRequest;
import com.cruise.dto.user.UserResponse;
import com.cruise.entity.Role;
import com.cruise.entity.User;
import com.cruise.exception.ApiException;
import com.cruise.repository.RoleRepository;
import com.cruise.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // Получить всех пользователей (для ADMIN и OWNER)
    public List<UserResponse> getAll() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Получить пользователя по id
    public UserResponse getById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Пользователь с id=" + id + " не найден"));
        return toResponse(user);
    }

    // Получить пользователя по email (используется внутри)
    public UserResponse getByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> ApiException.notFound("Пользователь не найден"));
        return toResponse(user);
    }

    // Создать пользователя (для OWNER — с указанием роли)
    @Transactional
    public UserResponse create(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw ApiException.conflict("Пользователь с таким email уже существует");
        }

        String roleName = (request.getRole() != null) ? request.getRole() : "USER";
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> ApiException.badRequest("Роль '" + roleName + "' не найдена"));

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setAge(request.getAge());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);

        return toResponse(userRepository.save(user));
    }

    // Удалить пользователя (только OWNER)
    @Transactional
    public void delete(Integer id) {
        if (!userRepository.existsById(id)) {
            throw ApiException.notFound("Пользователь с id=" + id + " не найден");
        }
        userRepository.deleteById(id);
    }

    // Маппинг entity → DTO (приватный, используется внутри сервиса)
    public UserResponse toResponse(User user) {
        UserResponse dto = new UserResponse();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setAge(user.getAge());
        dto.setRole(user.getRole().getName());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}