package com.ecommerce.ecommerce.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Kullanıcı adı zorunludur")
    @Size(min = 3, max = 50, message = "Kullanıcı adı 3 ile 50 karakter arasında olmalıdır")
    private String firstName;

    @NotBlank(message = "Kullanıcı Soyadı zorunludur")
    private String lastName;

    @NotBlank(message = "E-posta adresi zorunludur")
    @Email(message = "Geçerli bir e-posta adresi giriniz")
    private String email;

    @NotBlank(message = "Şifre zorunludur")
    @Size(min = 6, message = "Şifre en az 6 karakter olmalıdır")
    private String password;

    @NotBlank(message = "Şifre onayı zorunludur")
    private String passwordConfirm;

    @NotBlank(message = "Telefon numarası zorunludur")
    private String phone;
}
