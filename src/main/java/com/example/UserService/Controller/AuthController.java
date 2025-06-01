package com.example.UserService.Controller;

import com.example.UserService.Dto.LoginRequestDto;
import com.example.UserService.Dto.LoginResponseDto;
import com.example.UserService.Dto.SignUpRequestDto;
import com.example.UserService.Dto.SignUpResponseDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @PostMapping("/signUp")
    public SignUpResponseDto signUp(SignUpRequestDto signUpRequestDto) {
        return null;
    }

    @PostMapping("/login")
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        return null;
    }
}
