package com.ecommerce.ecommerce.auth.security;

import com.ecommerce.ecommerce.auth.service.CustomUserDetailsService;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // @PreAuthorize @Secured gibi anotasyonlar için
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            CustomUserDetailsService userDetailsService,
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Security Filter Chain - HTTP Güvenlik Yapılandırması (spring security 5.7+ ile birlikte gelen yeni yapılandırma şekli)
     *
     * Konfigurasyon:
     * - CSRF koruması devre dışı bırakıldı (JWT kullanıyoruz,stateless API'ler için genellikle devre dışı bırakılır)
     * - Session: STATELESS (Her istek token ile doğrulanacak, sunucu tarafında oturum tutulmayacak)
     * - Public endpoints: /api/auth/** , /swagger-ui/**, /v3/api-docs/** (kayıt,giriş,token yenileme gibi işlemler için)
     * - Diğer tüm endpointler authentication gerektirir.
     */

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                //CSRF korumasını devre dışı bırakılır
                .csrf(csrf -> csrf.disable())

                //endpoint erişim kuralları
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints (Kimlik doğrulaması gerektirmeyen endpointler)
                        .requestMatchers(
                                "/api/auth/register",
                                "/api/auth/login",
                                "/api/auth/refresh",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                                ).permitAll()
                        // Admin endpoint'leri (gelecekte)
                        //.requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Diğer tüm endpointler kimlik doğrulaması gerektirir
                        .anyRequest().authenticated()
                )
                // Session yönetimi - stateless
                .sessionManagement(session -> session
                        .sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )
                // Exception handling - 401/403 için özel handler
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(401, "Unauthorized");
                        })
                )
                // JWT Authentication provider eklenir
                .authenticationProvider(
                        authenticationProvider()
                )
                // JWT Authentication filter eklenir (her istekte token kontrolü)
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );
        return http.build();
    }
    /**
     * PasswordEncoder Bean
     * *
     * Bcrypt algoritması kullanılır
     * - Her şifre için farklı bir salt(hashlemeden önce eklenen ek veri) üretir
     * - brute-force saldırılarına karşı dayanıklıdır
     * - Strength parametresi ile hashleme zorluğu ayarlanabilir (varsayılan 10)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    /**
     * Authentication Provider Bean
     * *
     * Kullanıcı kimlik doğrulama işlemlerini yönetir:
     * - UserDetailsService: Kullanıcı bilgilerini veritabanından yükler
     * - PasswordEncoder: Şifreleri doğrulamak için kullanılır
     */
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    /**
     * Authentication Manager Bean
     * *
     * Spring Security'nin kimlik doğrulama işlemlerini yöneten ana bileşenidir
     * - SecurityConfig'de tanımlanan AuthenticationProvider'ları kullanır
     * - AuthService'de login işleminde kullanılır
     */

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
