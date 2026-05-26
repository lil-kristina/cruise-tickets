package com.cruise.service;

import com.cruise.dto.auth.AuthResponse;
import com.cruise.dto.auth.LoginRequest;
import com.cruise.dto.auth.RegisterRequest;
import com.cruise.entity.Role;
import com.cruise.entity.User;
import com.cruise.exception.ApiException;
import com.cruise.repository.RoleRepository;
import com.cruise.repository.UserRepository;
import com.cruise.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Проверка — email уже занят
        if (userRepository.existsByEmail(request.getEmail())) {
            throw ApiException.conflict("Пользователь с таким email уже существует");
        }

        // Роль по умолчанию — USER
        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> ApiException.notFound("Роль USER не найдена"));

        // Создаём пользователя
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setAge(request.getAge());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);

        userRepository.save(user);

        String token = jwtUtil.generate(user.getEmail(), role.getName(), user.getId());
        return new AuthResponse(token, role.getName(), user.getId(), user.getName());
    }

    public AuthResponse login(LoginRequest request) {
        // Ищем пользователя по email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> ApiException.badRequest("Неверный email или пароль"));

        // Проверяем пароль
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw ApiException.badRequest("Неверный email или пароль");
        }

        String role = user.getRole().getName();
        String token = jwtUtil.generate(user.getEmail(), role, user.getId());
        return new AuthResponse(token, role, user.getId(), user.getName());
    }
}