package com.example.UserService.Services;

import com.example.UserService.Exception.UserAlreadyExistsException;
import com.example.UserService.Models.User;
import com.example.UserService.Repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean signUp(String email, String password) {
        if (userRepository.findUserByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException("User with Email " + email + " already exists");
        }
        
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        userRepository.save(user);
        return true;
    }

    public String login(String email, String password) {
        return "Token";
    }
}
