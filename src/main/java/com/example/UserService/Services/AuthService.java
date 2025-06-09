package com.example.UserService.Services;

import com.example.UserService.Exception.UserAlreadyExistsException;
import com.example.UserService.Exception.UserNotFoundException;
import com.example.UserService.Exception.WrongPasswordException;
import com.example.UserService.Models.User;
import com.example.UserService.Repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public boolean signUp(String email, String password) throws UserAlreadyExistsException {
        if (userRepository.findUserByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException("User with Email " + email + " already exists");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        userRepository.save(user);
        return true;
    }

    public String login(String email, String password) throws UserNotFoundException, WrongPasswordException {
        Optional<User> userOptional = userRepository.findUserByEmail(email);

        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User with Email " + email + " not found");
        }

        boolean matches = bCryptPasswordEncoder.matches(password, userOptional.get().getPassword());
        if (!matches) {
            throw new WrongPasswordException("Wrong password");
        }

        return "Token";
    }
}
