package com.ecommerce.ecommerce.auth.service;

import com.ecommerce.ecommerce.auth.dto.request.LoginRequest;
import com.ecommerce.ecommerce.auth.dto.request.RegisterRequest;
import com.ecommerce.ecommerce.auth.dto.response.LoginResponse;
import com.ecommerce.ecommerce.auth.dto.response.LogoutResponse;
import com.ecommerce.ecommerce.auth.dto.response.RegisterResponse;
import com.ecommerce.ecommerce.auth.dto.response.TokenResponse;
import com.ecommerce.ecommerce.user.model.Role;
import com.ecommerce.ecommerce.user.model.User;
import com.ecommerce.ecommerce.user.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RedisService redisService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;


    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            RedisService redisService,
            AuthenticationManager authenticationManager,
            CustomUserDetailsService userDetailsService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.redisService = redisService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail())!= null) {
            throw new RuntimeException("Bu e-posta zaten kullanılıyor.");
        }

        if (userRepository.findByPhone(request.getPhone())!= null) {
            throw new RuntimeException("Bu telefon numarası zaten kullanılıyor.");
        }

        if(!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new RuntimeException("Şifreler eşleşmiyor.");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));


        user.setRole(Role.USER);
        user.setActive(true);


        User savedUser = userRepository.save(user);

        return RegisterResponse.builder()
                .success(true)
                .message("Kayıt başarılı! Giriş yapabilirsiniz.")
                .user(RegisterResponse.UserInfo.builder()
                        .id(savedUser.getId())
                        .firstName(savedUser.getFirstName())
                        .lastName(savedUser.getLastName())
                        .email(savedUser.getEmail())
                        .phone(savedUser.getPhone())
                        .build())
                .registeredAt(LocalDateTime.now())
                .build();
    }

    @Transactional
    public LoginResponse login(LoginRequest request, String ipAddress, String userAgent){

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getPhoneOrEmail(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getPhoneOrEmail());

        User user = userRepository.findByEmail(request.getPhoneOrEmail());

        if(user == null){
            user = userRepository.findByPhone(request.getPhoneOrEmail());
        }

        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        Map<String, String> metadata = new HashMap<>();
        metadata.put("ipAddress", ipAddress);
        metadata.put("userAgent", userAgent);
        metadata.put("deviceType",detectDeviceType(userAgent));

        redisService.saveSession(
                user.getId().toString(),
                accessToken,
                refreshToken,
                metadata,
                7 * 24 * 60 * 60 * 1000L // 7 gün (milisaniye)
        );

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(15 * 60 * 1000L) // 15 dakika (access token süresi)
                .user(LoginResponse.UserInfo.builder()
                        .id(user.getId().toString())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .phone(user.getPhone())
                        .role(user.getRole().name())
                        .build())
                .build();
    }

    @Transactional
    public TokenResponse refreshToken(String refreshToken){

        String username = jwtService.extractUsername(refreshToken);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        User user = userRepository.findByEmail(username);
        if (user == null){
            user = userRepository.findByPhone(username);
        }

        if (!jwtService.isTokenValid(refreshToken, userDetails)) {
            throw new RuntimeException("Geçersiz refresh token.");
        }

        if(!redisService.isRefreshTokenValid(user.getId().toString(), refreshToken)){
            throw new RuntimeException("Refresh token Redis'te bulunamadı veya geçersiz.");
        }

        String newAccessToken = jwtService.generateToken(userDetails);

        redisService.updateAccessToken(user.getId().toString(), newAccessToken);

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(15 * 60 * 1000L) // 15 dakika
                .build();
    }

    public LogoutResponse logout(String userId){
        redisService.deleteSession(userId);
        return LogoutResponse.builder()
                .success(true)
                .message("Başarıyla çıkış yapıldı.")
                .logoutAt(LocalDateTime.now())
                .build();
    }

    private String detectDeviceType(String userAgent){
        if (userAgent == null) {
            return "Unknown";
        }

        userAgent = userAgent.toLowerCase();

        if (userAgent.contains("mobile") || userAgent.contains("android") || userAgent.contains("iphone")) {
            return "Mobile";
        } else if (userAgent.contains("tablet") || userAgent.contains("ipad")) {
            return "Tablet";
        } else {
            return "Desktop";
        }
    }

    public boolean validateToken(String token, String userId){
        return redisService.isAccessTokenValid(userId, token);
    }



}
