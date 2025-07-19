package com.example.UserService.Exception;

public class LoginSessionExpiredException extends RuntimeException {
  public LoginSessionExpiredException() {
  }

  public LoginSessionExpiredException(String message) {
        super(message);
    }
}
