package com.ecommerce.ecommerce.auth.controller;

import com.ecommerce.ecommerce.auth.dto.request.LoginRequest;
import com.ecommerce.ecommerce.auth.dto.request.RegisterRequest;
import com.ecommerce.ecommerce.auth.dto.request.TokenRequest;
import com.ecommerce.ecommerce.auth.dto.response.LoginResponse;
import com.ecommerce.ecommerce.auth.dto.response.LogoutResponse;
import com.ecommerce.ecommerce.auth.dto.response.RegisterResponse;
import com.ecommerce.ecommerce.auth.dto.response.TokenResponse;
import com.ecommerce.ecommerce.auth.security.UserPrincipal;
import com.ecommerce.ecommerce.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request
    ){
        RegisterResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest
            ){
        String ipAdress = getClientIpAddress(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

        LoginResponse response = authService.login(request, ipAdress, userAgent);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(
            @Valid @RequestBody TokenRequest request
    ){
        TokenResponse response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        String userId = userPrincipal.getId().toString();

        LogoutResponse response = authService.logout(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate")
    public ResponseEntity<String> validateToken(){
        return ResponseEntity.ok("Token geçerli");
    }

    private String getClientIpAddress(HttpServletRequest request){
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null){
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
}
