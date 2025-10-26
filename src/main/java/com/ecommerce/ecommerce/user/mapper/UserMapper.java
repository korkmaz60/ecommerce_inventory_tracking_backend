package com.ecommerce.ecommerce.user.mapper;

import com.ecommerce.ecommerce.auth.dto.request.RegisterRequest;
import com.ecommerce.ecommerce.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", expression = "java(true)")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    User toEntity(RegisterRequest request);

    // UserResponse toDto(User user);  // TODO: UserResponse sınıfı yoksa kaldırıldı


}
