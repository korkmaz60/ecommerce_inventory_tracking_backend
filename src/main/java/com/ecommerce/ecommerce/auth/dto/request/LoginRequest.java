package com.ecommerce.ecommerce.auth.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class LoginRequest {
    @NotBlank(message = "Kullanıcı adı veya e-posta zorunludur")
    private String phoneOrEmail;
    
    @NotBlank(message = "Şifre zorunludur")
    private String password;
}