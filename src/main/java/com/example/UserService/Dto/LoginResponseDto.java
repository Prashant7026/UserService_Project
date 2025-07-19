package com.example.UserService.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDto {
    private RequestStatus status;
    private String message;
}
