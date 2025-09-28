package com.ecommerce.ecommerce.user.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class UserListResponse {
    private List<UserSummary> users;
    private int totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    
    @Data
    public static class UserSummary {
        private Long id;
        private String username;
        private String email;
        private boolean active;
    }
}