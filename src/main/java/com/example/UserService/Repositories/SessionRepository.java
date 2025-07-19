package com.example.UserService.Repositories;

import com.example.UserService.Models.Session;
import com.example.UserService.Models.SessionStatus;
import com.example.UserService.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {
    Session save(Session session);

    List<Session> getAllByUser_Id(Long userId);

    Session getFirstByToken(String token);

    Long user(User user);
}
