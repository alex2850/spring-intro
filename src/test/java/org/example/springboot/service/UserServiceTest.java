package org.example.springboot.service;

import org.example.springboot.dto.UserRegistrationRequestDto;
import org.example.springboot.dto.UserResponseDto;
import org.example.springboot.enums.RoleName;
import org.example.springboot.exception.EntityNotFoundException;
import org.example.springboot.exception.RegistrationException;
import org.example.springboot.mapper.UserMapper;
import org.example.springboot.model.Role;
import org.example.springboot.model.User;
import org.example.springboot.repository.RoleRepository;
import org.example.springboot.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("register() — should register new user and return UserResponseDto")
    public void register_ShouldReturnUserResponse_Ok() {
        UserRegistrationRequestDto request = new UserRegistrationRequestDto();
        request.setEmail("test@mail.com");
        request.setPassword("123456");
        request.setFirstName("Bob");

        User user = new User();
        user.setEmail("test@mail.com");
        user.setPassword("encoded");

        Role role = new Role();
        role.setName(RoleName.ROLE_USER);

        UserResponseDto dto = new UserResponseDto();
        dto.setEmail("test@mail.com");
        dto.setFirstName("Bob");

        when(userRepository.existsByEmail("test@mail.com")).thenReturn(false);

        when(userMapper.toModel(request)).thenReturn(user);

        when(passwordEncoder.encode("123456")).thenReturn("encoded");

        when(roleRepository.findByName(RoleName.ROLE_USER)).thenReturn(Optional.of(role));

        when(userMapper.toDto(user)).thenReturn(dto);

        UserResponseDto actual = userService.register(request);

        assertEquals("test@mail.com", actual.getEmail());
        assertEquals("Bob", actual.getFirstName());

        verify(userRepository).save(user);
        assertEquals(Set.of(role), user.getRoles());
        assertEquals("encoded", user.getPassword());

        verify(userRepository).existsByEmail("test@mail.com");
        verify(userMapper).toModel(request);
        verify(passwordEncoder).encode("123456");
        verify(roleRepository).findByName(RoleName.ROLE_USER);
        verify(userRepository).save(user);
        verify(userMapper).toDto(user);
    }

    @Test
    @DisplayName("register() — should throw RegistrationException if email exists")
    public void register_WhenEmailExists_Throw() {
        UserRegistrationRequestDto request = new UserRegistrationRequestDto();
        request.setEmail("test@mail.com");

        when(userRepository.existsByEmail("test@mail.com")).thenReturn(true);

        assertThrows(RegistrationException.class, () -> userService.register(request));

        verify(userRepository).existsByEmail("test@mail.com");
    }

    @Test
    @DisplayName("register() — should throw EntityNotFoundException if role not found")
    public void register_WhenRoleNotFound_Throw() {
        UserRegistrationRequestDto request = new UserRegistrationRequestDto();
        request.setEmail("new@mail.com");
        request.setPassword("123");

        User user = new User();
        user.setEmail("new@mail.com");

        when(userRepository.existsByEmail("new@mail.com")).thenReturn(false);

        when(userMapper.toModel(request)).thenReturn(user);

        when(passwordEncoder.encode("123")).thenReturn("encoded");

        when(roleRepository.findByName(RoleName.ROLE_USER)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.register(request));

        verify(userRepository).existsByEmail("new@mail.com");
        verify(userMapper).toModel(request);
        verify(passwordEncoder).encode("123");
        verify(roleRepository).findByName(RoleName.ROLE_USER);
    }
}
