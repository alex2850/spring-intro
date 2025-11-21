package org.example.springboot;

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
import org.example.springboot.service.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

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

    // -------------------------------------------------------------------------
    @Test
    @DisplayName("register() — should register new user and return UserResponseDto")
    public void register_ShouldReturnUserResponse() {
        // given
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

        Mockito.when(userRepository.existsByEmail("test@mail.com"))
                .thenReturn(false);

        Mockito.when(userMapper.toModel(request)).thenReturn(user);

        Mockito.when(passwordEncoder.encode("123456")).thenReturn("encoded");

        Mockito.when(roleRepository.findByName(RoleName.ROLE_USER))
                .thenReturn(Optional.of(role));

        Mockito.when(userMapper.toDto(user)).thenReturn(dto);

        // when
        UserResponseDto actual = userService.register(request);

        // then
        Assertions.assertEquals("test@mail.com", actual.getEmail());
        Assertions.assertEquals("Bob", actual.getFirstName());

        Mockito.verify(userRepository).save(user);
        Assertions.assertEquals(Set.of(role), user.getRoles());
        Assertions.assertEquals("encoded", user.getPassword());
    }

    // -------------------------------------------------------------------------
    @Test
    @DisplayName("register() — should throw RegistrationException if email exists")
    public void register_ShouldThrow_WhenEmailExists() {
        UserRegistrationRequestDto request = new UserRegistrationRequestDto();
        request.setEmail("test@mail.com");

        Mockito.when(userRepository.existsByEmail("test@mail.com"))
                .thenReturn(true);

        Assertions.assertThrows(RegistrationException.class,
                () -> userService.register(request));
    }

    // -------------------------------------------------------------------------
    @Test
    @DisplayName("register() — should throw EntityNotFoundException if role not found")
    public void register_ShouldThrow_WhenRoleNotFound() {
        UserRegistrationRequestDto request = new UserRegistrationRequestDto();
        request.setEmail("new@mail.com");
        request.setPassword("123");

        User user = new User();
        user.setEmail("new@mail.com");

        Mockito.when(userRepository.existsByEmail("new@mail.com"))
                .thenReturn(false);

        Mockito.when(userMapper.toModel(request)).thenReturn(user);

        Mockito.when(passwordEncoder.encode("123")).thenReturn("encoded");

        Mockito.when(roleRepository.findByName(RoleName.ROLE_USER))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> userService.register(request));
    }
}
