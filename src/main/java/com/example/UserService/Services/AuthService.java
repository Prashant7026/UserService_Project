package com.example.UserService.Services;

import com.example.UserService.Exception.*;
import com.example.UserService.Models.Session;
import com.example.UserService.Models.SessionStatus;
import com.example.UserService.Models.User;
import com.example.UserService.Repositories.SessionRepository;
import com.example.UserService.Repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class AuthService {
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    // private SecretKey key = Jwts.SIG.HS256.key().build();               // HS256 is a algorithm
    private SecretKey key = Keys.hmacShaKeyFor(
            "ramshrihitharivanshprashantisisisisisveryveryveryveryverycool".getBytes(StandardCharsets.UTF_8));

    private SessionRepository sessionRepository;

    public AuthService(UserRepository userRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder,
                       SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.sessionRepository = sessionRepository;
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

    public String login(String email, String password) throws UserNotFoundException, WrongPasswordException, LoginLimitReachedException {
        Optional<User> userOptional = userRepository.findUserByEmail(email);

        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User with Email " + email + " not found");
        }

        boolean matches = bCryptPasswordEncoder.matches(password, userOptional.get().getPassword());
        if (!matches) {
            throw new WrongPasswordException("Wrong password");
        }

        // When user login in three devices then ask for logout from other devices
        if (loginLimitReached(userOptional.get().getId())) {
            throw new LoginLimitReachedException("You have reached the maximum number of login sessions");
        }

        String token = createJwtToken(userOptional.get().getId(), new ArrayList<>(), userOptional.get().getEmail());

        // Save session
        Session session = new Session();
        session.setToken(token);
        session.setUser(userOptional.get());
        // Expiration of 30 days from now
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, 30);
        Date datePlus30Days = calendar.getTime();

        session.setExpiringAt(datePlus30Days);
        session.setSessionStatus(SessionStatus.ACTIVE);
        sessionRepository.save(session);

        return token;
    }

    public Boolean validate(String token) throws Exception {
        try {
            Jws<Claims> claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);

            // We can do all the checks/validations here
            Date expriyDate = claims.getPayload().getExpiration();
            Calendar calendar = Calendar.getInstance();
            Date currentDate = calendar.getTime();
            if (expriyDate.equals(currentDate)) {
                String userId = claims.getPayload().getId();
                Session expiredSession = sessionRepository.getFirstByToken(token);
                expiredSession.setSessionStatus(SessionStatus.ENDED);
                sessionRepository.save(expiredSession);
                throw new LoginSessionExpiredException("Session has expired");
            }
            Long userId = claims.getPayload().get("user_id", Long.class);
        } catch (Exception e) {
            throw e;
        }

        return true;
    }

    private String createJwtToken(Long userId, List<String> roles, String email) {
        Map<String, Object> dataInJwt = new HashMap<>();
        dataInJwt.put("user_id", userId);
        dataInJwt.put("roles", roles);
        dataInJwt.put("email", email);

        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, 30);
        Date datePlus30Days = calendar.getTime();

        String token = Jwts.builder()
                .claims(dataInJwt)
                .expiration(datePlus30Days)
                .issuedAt(new Date())
                .signWith(key)
                .compact();

        return token;
    }

    private Boolean loginLimitReached(Long userId) {
        List<Session> getAllSessions = sessionRepository.getAllByUser_Id(userId);
        int activeSessions = 0;
        for (Session session : getAllSessions) {
            if (session.getSessionStatus().equals(SessionStatus.ACTIVE)) {
                activeSessions++;
            }
        }
        if (activeSessions == 3) {
            return true;
        }

        return false;
    }
}
