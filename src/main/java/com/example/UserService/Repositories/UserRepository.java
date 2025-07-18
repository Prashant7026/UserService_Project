package com.example.UserService.Repositories;

import com.example.UserService.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByEmail(String email);

    Long findIdByEmail(String email);
}
