package com.ecommerce.ecommerce.auth.dto.request;



@Getter
@Setter
public class TokenRequest {
    private String refreshToken;
    private String accessToken;
    private Long expiresIn; // access token expiration time in milliseconds
}
