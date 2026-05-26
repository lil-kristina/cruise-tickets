package com.cruise.dto.user;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Integer id;
    private String name;
    private String email;
    private Integer age;
    private String role;
    private LocalDateTime createdAt;
}