package com.ecommerce.ecommerce.user.dto.request;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Data
public class UserUpdateRequest {
    @Size(min = 3, max = 50, message = "Kullanıcı adı 3 ile 50 karakter arasında olmalıdır")
    private String username;
    
    @Email(message = "Geçerli bir e-posta adresi giriniz")
    private String email;
    
    private String firstName;
    private String lastName;
    private Boolean active;
}