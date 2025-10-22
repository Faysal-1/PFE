package com.maroctbib.modules.auth.mapper;

import com.maroctbib.modules.auth.domain.User;
import com.maroctbib.modules.auth.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    UserDto toDto(User user);
    
    User toEntity(UserDto userDto);
}
