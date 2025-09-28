package com.ecommerce.ecommerce.auth.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private Long expiresIn; // access token expiration time in milliseconds
}
