package fit.biejk.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import fit.biejk.entity.Client;
import fit.biejk.entity.Specialist;
import fit.biejk.entity.User;
import fit.biejk.entity.UserRole;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.List;

@Slf4j
@ApplicationScoped
public class AuthService {

    @Inject
    SpecialistService specialistService;

    @Inject
    ClientService clientService;

    @Inject
    UserService userService;

    @Inject
    SecurityIdentity securityIdentity;

    public String signUp(String email, String password, UserRole role) {
        log.info("Sign up: email={}, role={}", email, role);
        String jwtToken = "";
        if (role == UserRole.SPECIALIST) {
            Specialist specialist = new Specialist();
            specialist.setEmail(email);
            specialist.setPassword(hashPassword(password));
            specialist.setRole(role);
            Specialist newSpecialist = specialistService.create(specialist);
            log.debug("Created specialist with ID={}", newSpecialist.getId());
            jwtToken = generateJWT(newSpecialist, role);
        } else if (role == UserRole.CLIENT) {
            Client client = new Client();
            client.setEmail(email);
            client.setPassword(hashPassword(password));
            client.setRole(role);
            Client newClient = clientService.create(client);
            log.debug("Created client with ID={}", newClient.getId());
            jwtToken = generateJWT(newClient, role);
        }
        log.debug("Sign up JWT={}", jwtToken);
        return jwtToken;
    }

    public String signIn(String email, String password, UserRole role) {
        log.info("Sign in: email={}, role={}", email, role);
        User user = userService.getByEmail(email);
        verifyPassword(password, user.getPassword());
        if (user.getRole() != role) {
            log.error("Invalid role: expected={}, actual={}", role, user.getRole());
            throw new IllegalArgumentException("Invalid role");
        }
        String token = generateJWT(user, role);
        log.debug("Sign in JWT={}", token);
        return token;
    }

    private String generateJWT(User user, UserRole role) {
        return Jwt.issuer("quarkus-app")
                .subject(user.getId().toString())
                .claim("groups", List.of(user.getRole().name()))
                .expiresIn(Duration.ofHours(24))
                .sign();
    }

    private boolean verifyPassword(String password, String hashed) {
        return BCrypt.verifyer().verify(password.toCharArray(), hashed).verified;
    }

    public Long getCurrentUserId() {
        log.debug("Current principal name={}", securityIdentity.getPrincipal().getName());
        return Long.parseLong(securityIdentity.getPrincipal().getName());
    }

    public boolean isCurrentUser(Long userId) {
        Long currentUserId = getCurrentUserId();
        log.debug("Comparing user IDs: current={}, requested={}", currentUserId, userId);
        return currentUserId.equals(userId);
    }

    private String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }
}
