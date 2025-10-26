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
public class LogoutResponse {

    boolean success;

    String message;

    private LocalDateTime logoutAt;
}
