package com.amigoscode.springboot;

import lombok.*;

@Data
@NoArgsConstructor // <--- Add this
@AllArgsConstructor
public class LoginRequest {
    private String username;
    private String password;
}
