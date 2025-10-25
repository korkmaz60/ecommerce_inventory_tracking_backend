package com.ecommerce.ecommerce.auth.service;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


// Redis = In-Memory Database (RAM'de çalışan veritabanı)
//  Neden kullanıyoruz?
//  - Çok hızlı (RAM'de çalışır)
//  - Key-Value şeklinde veri tutar
//  - Otomatik silme özelliği var (TTL - Time To Live)
//  - Oturum yönetimi için ideal



@Service
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /*
     *Token'ı Redis'e kaydetme işlemi
     * @param ttl -> Token'ın geçerlilik süresi (milisaniye)
     * @param metadata Ek bilgiler (device, IP vs.)
     */
    public void saveSession(String userId, String accessToken, String refreshToken, Map<String, String> metadata, long ttl){

        String key = generateSessionKey(userId);

        Map<String,String> sessionData = new HashMap<>();
        sessionData.put("accessToken",accessToken);
        sessionData.put("refreshToken",refreshToken);
        sessionData.put("userId",userId);
        sessionData.put("loginTime", LocalDateTime.now().toString());
        sessionData.put("lastActivity", LocalDateTime.now().toString());

        // Metadata ekleme (device, IP vs.)
        if (metadata != null){
            sessionData.putAll(metadata);
        }

        // Hash'i redis'e kaydeder
        redisTemplate.opsForHash().putAll(key, sessionData);

        //TTL ayarlanır (refresh token süresi kadar)
        redisTemplate.expire(key, ttl, TimeUnit.MILLISECONDS);
    }
    public String getAccessToken(String userId){
        String key = generateSessionKey(userId);
        Object token = redisTemplate.opsForHash().get(key,"accessToken");
        return token != null ? token.toString() : null;
    }

    public String getRefreshToken(String userId){
        String key = generateSessionKey(userId);
        Object token = redisTemplate.opsForHash().get(key, "refreshToken");
        return token != null ? token.toString() : null;
    }

    public Map<Object, Object> getSession(String userId){
        String key = generateSessionKey(userId);
        return redisTemplate.opsForHash().entries(key);
    }

    public void updateAccessToken (String userId,String newAccessToken){
        String key = generateSessionKey(userId);
        redisTemplate.opsForHash().put(key,"accessToken",newAccessToken);
        redisTemplate.opsForHash().put(key,"newAccessToken",newAccessToken);
        redisTemplate.opsForHash().put(key,"lastActivity",LocalDateTime.now().toString());
    }

    public void updateLastActivity(String userId){
        String key = generateSessionKey(userId);
        redisTemplate.opsForHash().put(key, "lastActivity",LocalDateTime.now().toString());
    }

    public void deleteSession(String userId){
        String key = generateSessionKey(userId);
        redisTemplate.delete(key);
    }
    public boolean isAccessTokenValid(String userId, String token){
        String storedToken = getAccessToken(userId);
        return storedToken != null && storedToken.equals(token);
    }

    public boolean isRefreshTokenValid(String userId, String token){
        String storedToken = getRefreshToken(userId);
        return storedToken != null && storedToken.equals(token);
    }

    public Long getSessionExpiration(String userId){
        String key = generateSessionKey(userId);
        return redisTemplate.getExpire(key,TimeUnit.MILLISECONDS);
    }

    public boolean sessionExists(String userId){
        String key = generateSessionKey(userId);
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    private String generateSessionKey(String userId){
        return "user:" + userId + ":session";
    }
}
