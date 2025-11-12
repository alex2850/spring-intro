package org.example.springboot.service;

import lombok.RequiredArgsConstructor;
import org.example.springboot.dto.UserRegistrationRequestDto;
import org.example.springboot.dto.UserResponseDto;
import org.example.springboot.exception.EntityNotFoundException;
import org.example.springboot.mapper.UserMapper;
import org.example.springboot.model.User;
import org.example.springboot.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("Can not find book by email "
                        + requestDto.getEmail()));

        userMapper.updateUserFromDto(requestDto, user);

        return userMapper.toResponseDto(user);
    }
}
