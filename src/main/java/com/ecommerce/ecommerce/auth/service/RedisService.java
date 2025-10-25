package com.ecommerce.ecommerce.auth.service;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


// Redis = In-Memory Database (RAM'de çalışan veritabanı)
//  Neden kullanıyoruz?
//  - Çok hızlı (RAM'de çalışır)
//  - Key-Value şeklinde veri tutar
//  - Otomatik silme özelliği var (TTL - Time To Live)
//  - Oturum yönetimi için ideal



@Service
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;

    public RedisService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /*
     *Token'ı Redis'e kaydetme işlemi
     * @param token -> JWT token
     * @param ttl -> Token'ın geçerlilik süresi (milisaniye)
     *
     */
    public void saveToken(String userId, String token, long ttl){
        String key = generateKey(userId);
        redisTemplate.opsForValue().set(key, token, ttl, TimeUnit.MILLISECONDS);
    }

    /*
     * Redis'ten token okuma işlemi
     * @return Token (varsa), yoksa null
     */
    public String getToken(String userId){
        String key = generateKey(userId);
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteToken(String userId){
        String key = generateKey(userId);
        redisTemplate.delete(key);
    }

    /*
     * Token geçerlilik kontrolü
     */
    public boolean isTokenValid(String userId, String token){
        String storedToken = getToken(userId);
        return storedToken != null && storedToken.equals(token);
    }

    /*
     * Token'ın kalan süresini öğrenme
     */
    public Long getTokenExpiration(String userId){
        String key = generateKey(userId);
        return redisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
    }

    private String generateKey(String userId){
        return "user:" + userId + ":token";
    }
}
