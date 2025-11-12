package org.example.springboot.mapper;

import org.example.springboot.config.MapperConfig;
import org.example.springboot.dto.UserRegistrationRequestDto;
import org.example.springboot.dto.UserResponseDto;
import org.example.springboot.model.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    User toModel(UserRegistrationRequestDto registrationRequestDto);

    UserResponseDto toResponseDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDto(UserRegistrationRequestDto requestDto, @MappingTarget User book);
}
