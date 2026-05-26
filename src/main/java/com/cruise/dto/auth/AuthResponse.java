package com.cruise.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;   // JWT токен для хранения в localStorage
    private String role;    // USER / ADMIN / OWNER — для управления доступом на фронте
    private Integer userId;
    private String name;
}