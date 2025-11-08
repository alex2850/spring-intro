package org.example.springboot.service;

import lombok.RequiredArgsConstructor;
import org.example.springboot.dto.UserRegistrationRequestDto;
import org.example.springboot.dto.UserResponseDto;
import org.example.springboot.mapper.UserMapper;
import org.example.springboot.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto) {
        return userMapper.toResponseDto(userRepository.save(userMapper.toModel(requestDto)));
    }
}
