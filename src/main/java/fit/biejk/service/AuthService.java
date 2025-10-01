package fit.biejk.service;

import fit.biejk.dto.ResetPasswordRequest;
import fit.biejk.entity.Client;
import fit.biejk.entity.Specialist;
import fit.biejk.entity.User;
import fit.biejk.entity.UserRole;
import fit.biejk.utilits.CryptoUtils;
import io.quarkus.redis.client.RedisClient;
import io.quarkus.security.identity.SecurityIdentity;
import io.vertx.redis.client.Response;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

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
     * Length of the verification code used for password reset (in digits).
     */
    private static final int PASSWORD_LENGTH = 6;

    /**
     * Service for sending emails.
     */
    @Inject
    private MailService mailService;

    /**
     * Service for managing locations.
     */
    @Inject
    private LocationService locationService;

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
     * Redis client for temporary storage (e.g., verification codes).
     */
    @Inject
    private RedisClient redisClient;

    @Inject
    private TokenService tokenService;

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
            specialist.setPassword(CryptoUtils.hash(password));
            specialist.setRole(role);
            specialist.setLocation(locationService.getDefault());
            Specialist newSpecialist = specialistService.create(specialist);
            log.debug("Created specialist with ID={}", newSpecialist.getId());
            jwtToken = tokenService.generateToken(newSpecialist);
        } else if (role == UserRole.CLIENT) {
            Client client = new Client();
            client.setEmail(email);
            client.setPassword(CryptoUtils.hash(password));
            client.setRole(role);
            client.setLocation(locationService.getDefault());
            Client newClient = clientService.create(client);
            log.debug("Created client with ID={}", newClient.getId());
            jwtToken = tokenService.generateToken(newClient);
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
        if (!CryptoUtils.verify(password, user.getPassword())) {
            log.error("Invalid password");
            throw new IllegalArgumentException("Invalid password");
        }

        if (user.getRole() != role) {
            log.error("Invalid role: expected={}, actual={}", role, user.getRole());
            throw new IllegalArgumentException("Invalid role");
        }
        String token = tokenService.generateToken(user);
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
     * Initiates the forgot password process by generating and sending a verification code.
     *
     * @param email user's email address
     */
    @Transactional
    public void forgotPassword(final String email) {
        User user = userService.getByEmail(email);
        if (user == null) {
            log.warn("Attempt to reset password for non-existent user: {}", email);
            return;
        }

        String code = CryptoUtils.generateNumericCode(PASSWORD_LENGTH);
        String hashedCode = CryptoUtils.hash(code);

        String key = "forgot-password:" + email;
        redisClient.setex(key, "600", hashedCode);

        String message = "Your password reset code is: " + code + "\nIt is valid for 10 minutes.";

        mailService.send(email, "Reset password", message);
        log.debug("Generated reset code for email={}: code={}, hashed={}", email, code, hashedCode);
    }

    /**
     * Resets the password using the provided verification code.
     *
     * @param request reset password request containing email, code, and new password
     * @return new JWT token after successful password reset
     * @throws IllegalArgumentException if validation fails
     */
    @Transactional
    public String resetPassword(final ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            log.error("Invalid new password: expected={}, actual={}",
                    request.getNewPassword(), request.getConfirmPassword());
            throw new IllegalArgumentException("Invalid new password");
        }

        String key = "forgot-password:" + request.getEmail();
        Response response = redisClient.get(key);
        String savedHashedCode = response != null ? response.toString() : null;

        if (!CryptoUtils.verify(request.getVerificationCode(), savedHashedCode)) {
            log.error("Invalid verification code");
            throw new IllegalArgumentException("Invalid verification code");
        }

        User user = userService.getByEmail(request.getEmail());
        User updatedUser = userService.updatePassword(user.getId(), request.getNewPassword());

        log.debug("Updated user with ID={}", updatedUser.getId());

        String res = tokenService.generateToken(updatedUser);
        log.debug("Change JWT={}", res);

        return res;
    }
}
