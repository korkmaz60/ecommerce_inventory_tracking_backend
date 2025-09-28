package com.ecommerce.ecommerce.user.service;

import com.ecommerce.ecommerce.user.dto.UserDTO;
import com.ecommerce.ecommerce.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserDTO findById(Long id) {
        // Implementation will be added
        return null;
    }

    public UserDTO createUser(UserDTO userDTO) {
        // Implementation will be added
        return null;
    }

    public UserDTO updateUser(Long id, UserDTO userDTO) {
        // Implementation will be added
        return null;
    }

    public void deleteUser(Long id) {
        // Implementation will be added
    }
}
