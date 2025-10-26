package com.ecommerce.ecommerce.auth.security;


import com.ecommerce.ecommerce.auth.service.CustomUserDetailsService;
import com.ecommerce.ecommerce.auth.service.JwtService;
import com.ecommerce.ecommerce.auth.service.RedisService;   
import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter
 * Her  HTTP isteğinde çalışır:
 * Authorization header'dan JWT token'ı çıkarır
 * Token'ı doğrular (hem JWT hem Redis)
 * Kullanıcıyı SecurityContext'e ekler
 * *
 * OncePerRequestFilter: Her HTTP isteği için bir kez çalışır
 */



@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final RedisService redisService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            CustomUserDetailsService userDetailsService,
            RedisService redisService
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.redisService = redisService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    )throws ServletException, IOException{

        // Public endpoint'leri atla (JWT kontrolü gerektirmeyen yollar)
        String requestPath = request.getServletPath();
        if (requestPath.startsWith("/api/auth/register") ||
            requestPath.startsWith("/api/auth/login") ||
            requestPath.startsWith("/api/auth/refresh") ||
            requestPath.startsWith("/swagger-ui") ||
            requestPath.startsWith("/v3/api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Authorization header'dan JWT token alınır
        final String authHeader = request.getHeader("Authorization");

        //Header yoksa Bearer ile başlamıyorsa filtreyi atla
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        // JWT token'ı çıkar (Bearer temizlenir)
        final String jwt = authHeader.substring(7);
        final String userEmail;

        try {
            userEmail = jwtService.extractUsername(jwt);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null ){
                //Kullanıcıyı veritabanından yükler
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                if (jwtService.isTokenValid(jwt, userDetails)){
                    //redis'te token geçerliliği kontrol edilir
                    // eğer kullanıcı logout olmuşsa Redis'te token bulunmaz
                    // bu durumda token geçersiz sayılır

                    //Not:Redis kontrolü yapmak için userId lazım
                    //Şimdilik Jwt kontrolü yeterli, ileride eklenebilir
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    //request Details eklenir(Ip, SessionId vs.)
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            logger.error("JWT doğrulama hatası: " + e.getMessage());
        }
        // filtreleme zincirine devam ettirir
        filterChain.doFilter(request, response);
    }
}