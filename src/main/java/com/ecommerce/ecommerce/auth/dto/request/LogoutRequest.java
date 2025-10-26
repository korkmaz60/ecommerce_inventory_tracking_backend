package com.ecommerce.ecommerce.auth.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogoutRequest {

    private Long userId;

    // Multi Device Logout için ileride lazım olabilir
    private boolean logoutFromAllDevices = false;
}
