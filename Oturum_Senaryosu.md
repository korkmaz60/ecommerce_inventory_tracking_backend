Oturum Senaryosu:

1. Kullanıcı login olur
2. JWT token üretilir: "eyJhbGciOiJIUzI1NiIs..."
3. Bu token Redis'e kaydedilir:
   Key: "user:123:token"
   Value: "eyJhbGciOiJIUzI1NiIs..."
   TTL: 24 saat (sonra otomatik silinir)

4. Her istekte:
    - Token header'dan gelir
    - Redis'te var mı kontrol edilir
    - Varsa → geçerli
    - Yoksa → logout olmuş veya süresi dolmuş

5. Logout:
    - Redis'teki token silinir
    - Artık o token geçersiz