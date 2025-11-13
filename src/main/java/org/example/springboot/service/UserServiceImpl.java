package org.example.springboot.service;

import lombok.RequiredArgsConstructor;
import org.example.springboot.dto.UserRegistrationRequestDto;
import org.example.springboot.dto.UserResponseDto;
import org.example.springboot.exception.RegistrationException;
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
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException(
                    String.format("User with this email: %s already exists", requestDto.getEmail())
            );
        }
        User user = userMapper.toModel(requestDto);
        user.setPassword(requestDto.getPassword());
        userRepository.save(user);
        return userMapper.toDto(user);
    }
}
