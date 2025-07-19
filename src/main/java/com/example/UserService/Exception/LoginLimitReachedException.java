package com.example.UserService.Exception;

public class LoginLimitReachedException extends RuntimeException {
    public LoginLimitReachedException() {
    }

    public LoginLimitReachedException(String message) {
        super(message);
    }
}
