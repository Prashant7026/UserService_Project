package com.example.UserService.Controller;

import com.example.UserService.Dto.*;
import com.example.UserService.Services.AuthService;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signUp")
    public ResponseEntity<SignUpResponseDto> signUp(@RequestBody SignUpRequestDto signUpRequestDto) {
        SignUpResponseDto signUpResponseDto = new SignUpResponseDto();

        try {
            if (authService.signUp(signUpRequestDto.getEmail(), signUpRequestDto.getPassword())) {
                signUpResponseDto.setStatus(RequestStatus.SUCCESS);
            } else {
                signUpResponseDto.setStatus(RequestStatus.FAILURE);
            }
            return ResponseEntity.ok(signUpResponseDto);
        } catch (Exception e) {
            signUpResponseDto.setStatus(RequestStatus.FAILURE);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(signUpResponseDto);
        }
    }

    @PostMapping("/login")
    // ResponseEntity is a Spring Class that allow you to set HTTPStatusCode, Header, etc.
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        LoginResponseDto loginResponseDto = new LoginResponseDto();

        try {
            String token = authService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());
            loginResponseDto.setStatus(RequestStatus.SUCCESS);

            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("AUTH_TOKEN", token);
            ResponseEntity<LoginResponseDto> response = new ResponseEntity<>(
                    loginResponseDto,
                    headers,
                    HttpStatus.OK
            );

            return response;
        } catch (Exception e) {
            loginResponseDto.setStatus(RequestStatus.FAILURE);
            ResponseEntity<LoginResponseDto> response = new ResponseEntity<>(
                    loginResponseDto,
                    null,
                    HttpStatus.BAD_REQUEST
            );
            return response;
        }
    }

    @GetMapping("/validateToken")
    public Boolean validateToken(@RequestParam("token") String token) {
        return authService.validate(token);
    }
}
