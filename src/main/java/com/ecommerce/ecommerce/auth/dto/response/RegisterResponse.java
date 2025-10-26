package com.ecommerce.ecommerce.auth.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {
    private boolean success;
    private String message;
    private UserInfo user;
    private LocalDateTime registeredAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo{
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
    }
}
