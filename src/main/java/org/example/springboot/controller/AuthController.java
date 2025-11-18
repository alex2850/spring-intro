package org.example.springboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.springboot.dto.UserLoginRequestDto;
import org.example.springboot.dto.UserLoginResponseDto;
import org.example.springboot.dto.UserRegistrationRequestDto;
import org.example.springboot.dto.UserResponseDto;
import org.example.springboot.exception.RegistrationException;
import org.example.springboot.security.AuthenticationService;
import org.example.springboot.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
@Tag(name = "User management", description = "Endpoints for managing users")
public class AuthController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    @Operation(summary = "Register user", description = "Register user")
    @PostMapping("/registration")
    public UserResponseDto register(@RequestBody @Valid UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        return userService.register(requestDto);
    }

    @Operation(summary = "Login user", description = "Login user")
    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto requestDto) {
        return authenticationService.authenticate(requestDto);
    }
}
