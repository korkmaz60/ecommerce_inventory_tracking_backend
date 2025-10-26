package com.ecommerce.ecommerce.auth.security;

import com.ecommerce.ecommerce.user.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

// UserPrincipal amacı, Spring Security'nin UserDetails arayüzünü uygulayarak kullanıcı bilgilerini güvenli bir şekilde temsil etmektir.
// İleride Google OAuth2 veya diğer kimlik doğrulama yöntemleri eklendiğinde, UserDetailsService arayüzü ile birlikte çalışarak farklı kullanıcı türlerini yönetmeyi kolaylaştırır.


public class UserPrincipal implements UserDetails {

    private final User user;

    public UserPrincipal(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isActive();
    }

    // User nesnesini dışarı açmak için
    public User getUser() {
        return user;
    }

    public Long getId() {
        return user.getId();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public String getPhone() {
        return user.getPhone();
    }
}
