package com.ecommerce.ecommerce.user.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "users",uniqueConstraints = {
        @UniqueConstraint(name = "uk_users_email", columnNames = "email"), // normalde
        @UniqueConstraint(name = "uk_users_phone", columnNames = "phone")
})

//TODO: Regex ile email, phone ,password  validasyonu ekle

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="phone", nullable = false, unique = true,length = 15)
    private String phone;


    @Column(name="email", nullable = false, unique = true,length = 50)
    private String email;

    @Column(name="password",nullable = false,length = 100)
    private String password;

    private LocalDateTime createdAt;

    private boolean active;


}
