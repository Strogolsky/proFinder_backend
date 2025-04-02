package fit.biejk.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import fit.biejk.dto.ChangePasswordRequest;
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

/**
 * Service responsible for authentication logic.
 * <p>
 * Handles sign-up, sign-in, JWT token generation, and user context resolution.
 * </p>
 */
@Slf4j
@ApplicationScoped
public class AuthService {
    /**
     * JWT token expiration time — 24 hours.
     */
    private static final Duration TOKEN_EXPIRATION = Duration.ofHours(24);

    /**
     * Cost factor for BCrypt password hashing.
     * Higher value increases security but also computation time.
     */
    private static final int BCRYPT_COST = 12;

    /**
     * Service for managing specialists.
     */
    @Inject
    private SpecialistService specialistService;

    /**
     * Service for managing clients.
     */
    @Inject
    private ClientService clientService;

    /**
     * Service for managing users.
     */
    @Inject
    private UserService userService;

    /**
     * Quarkus security context used to get the current authenticated identity.
     */
    @Inject
    private SecurityIdentity securityIdentity;

    /**
     * Registers a new user (client or specialist) and returns a JWT token.
     *
     * @param email    user email
     * @param password plain text password
     * @param role     user role
     * @return JWT token for the newly created user
     */
    public String signUp(final String email, final String password, final UserRole role) {
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

    /**
     * Authenticates a user by verifying email and password and returns a JWT token.
     *
     * @param email    user email
     * @param password plain text password
     * @param role     expected user role
     * @return signed JWT token
     */
    public String signIn(final String email, final String password, final UserRole role) {
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

    /**
     * Returns the ID of the currently authenticated user.
     *
     * @return user ID as Long
     */
    public Long getCurrentUserId() {
        log.debug("Current principal name={}", securityIdentity.getPrincipal().getName());
        return Long.parseLong(securityIdentity.getPrincipal().getName());
    }

    /**
     * Checks if the specified user ID matches the currently authenticated user.
     *
     * @param userId user ID to check
     * @return true if matches, false otherwise
     */
    public boolean isCurrentUser(final Long userId) {
        Long currentUserId = getCurrentUserId();
        log.debug("Comparing user IDs: current={}, requested={}", currentUserId, userId);
        return currentUserId.equals(userId);
    }

    /**
     * Generates a signed JWT token for the given user and role.
     *
     * @param user user entity
     * @param role user role
     * @return JWT token
     */
    private String generateJWT(final User user, final UserRole role) {
        return Jwt.issuer("quarkus-app")
                .subject(user.getId().toString())
                .claim("groups", List.of(user.getRole().name()))
                .expiresIn(TOKEN_EXPIRATION)
                .sign();
    }

    /**
     * Verifies that the plain text password matches the hashed password.
     *
     * @param password plain text password
     * @param hashed   hashed password
     * @return true if verified
     */
    private boolean verifyPassword(final String password, final String hashed) {
        return BCrypt.verifyer().verify(password.toCharArray(), hashed).verified;
    }

    /**
     * Hashes the given plain text password using BCrypt.
     *
     * @param password plain text password
     * @return hashed password string
     */
    private String hashPassword(final String password) {
        return BCrypt.withDefaults().hashToString(BCRYPT_COST, password.toCharArray());
    }
    /**
     * Changes the password of the currently authenticated user.
     *
     * Verifies the old password, checks the new password and confirmation,
     * updates the password in the database, and returns a new JWT token.
     *
     * @param request password change data (old, new, confirm)
     * @return new JWT token after successful password update
     * @throws IllegalArgumentException if validation fails
     */
    public String changePassword(final ChangePasswordRequest request) {
        log.info("Change password: request={}", request);
        Long currentUserId = getCurrentUserId();
        User user = userService.getById(currentUserId);
        if (!verifyPassword(request.getOldPassword(), user.getPassword())) {
            log.error("Invalid old password: expected={}, actual={}", request.getOldPassword(), user.getPassword());
            throw new IllegalArgumentException("Invalid old password");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            log.error("Invalid new password: expected={}, actual={}", request.getNewPassword(), user.getPassword());
            throw new IllegalArgumentException("Invalid new password");
        }
        String hashPassword = hashPassword(request.getNewPassword());
        user.setPassword(hashPassword);
        User updatedUser = userService.update(user.getId(), user);
        log.debug("Updated user with ID={}", updatedUser.getId());
        String res = generateJWT(updatedUser, updatedUser.getRole());
        log.debug("Change JWT={}", res);
        return res;
    }
}
