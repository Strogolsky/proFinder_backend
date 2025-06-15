package fit.biejk.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import fit.biejk.dto.ChangePasswordRequest;
import fit.biejk.dto.ResetPasswordRequest;
import fit.biejk.entity.Client;
import fit.biejk.entity.Specialist;
import fit.biejk.entity.User;
import fit.biejk.entity.UserRole;
import io.quarkus.redis.client.RedisClient;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.jwt.build.Jwt;
import io.vertx.redis.client.Response;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.RandomStringGenerator;

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
     * Length of the verification code used for password reset (in digits).
     */
    private static final int PASSWORD_LENGTH = 6;

    /**
     * Cost factor for BCrypt password hashing.
     * Higher value increases security but also computation time.
     */
    private static final int BCRYPT_COST = 12;

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
            specialist.setPassword(createHash(password));
            specialist.setRole(role);
            specialist.setLocation(locationService.getDefault());
            Specialist newSpecialist = specialistService.create(specialist);
            log.debug("Created specialist with ID={}", newSpecialist.getId());
            jwtToken = generateJWT(newSpecialist, role);
        } else if (role == UserRole.CLIENT) {
            Client client = new Client();
            client.setEmail(email);
            client.setPassword(createHash(password));
            client.setRole(role);
            client.setLocation(locationService.getDefault());
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
        if (!verifyHash(password, user.getPassword())) {
            log.error("Invalid password");
            throw new IllegalArgumentException("Invalid password");
        }

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
        if (!verifyHash(request.getOldPassword(), user.getPassword())) {
            log.error("Invalid old password: expected={}, actual={}", request.getOldPassword(), user.getPassword());
            throw new IllegalArgumentException("Invalid old password");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            log.error("Invalid new password: expected={}, actual={}", request.getNewPassword(), user.getPassword());
            throw new IllegalArgumentException("Invalid new password");
        }
        String hashPassword = createHash(request.getNewPassword());
        user.setPassword(hashPassword);
        User updatedUser = userService.updatePassword(user.getId(), user);
        log.debug("Updated user with ID={}", updatedUser.getId());
        String res = generateJWT(updatedUser, updatedUser.getRole());
        log.debug("Change JWT={}", res);
        return res;
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

        String code = generateCode();
        String hashedCode = createHash(code);

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

        if (!verifyHash(request.getVerificationCode(), savedHashedCode)) {
            log.error("Invalid verification code");
            throw new IllegalArgumentException("Invalid verification code");
        }

        User user = userService.getByEmail(request.getEmail());
        user.setPassword(createHash(request.getNewPassword()));
        User updatedUser = userService.updatePassword(user.getId(), user);

        log.debug("Updated user with ID={}", updatedUser.getId());

        String res = generateJWT(updatedUser, updatedUser.getRole());
        log.debug("Change JWT={}", res);

        return res;
    }


    /**
     * Changes the email address of the currently authenticated user.
     * <p>
     * This method performs the following steps:
     * <ul>
     *     <li>Retrieves the current user by ID.</li>
     *     <li>Checks if the new email is different from the current one.</li>
     *     <li>Verifies the provided password against the stored password hash.</li>
     *     <li>Updates the user's email address in the database.</li>
     *     <li>Sends a notification email to the old email address.</li>
     *     <li>Generates and returns a new JWT token.</li>
     * </ul>
     *
     * @param newEmail the new email address to set
     * @param password the current password of the user for verification
     * @return a new JWT token reflecting the updated email
     * @throws IllegalArgumentException if the new email matches the old email or if the password is invalid
     */
    @Transactional
    public String changeEmail(final String newEmail, final String password) {
        Long id = getCurrentUserId();

        log.info("Change email: id={}, newEmail={}, password={}", id, newEmail, password);
        User user = userService.getById(id);

        if (user.getEmail().equals(newEmail)) {
            log.warn("New email is same as old email");
            throw new IllegalArgumentException("New email is same as old email");
        }

        if (!verifyHash(password, user.getPassword())) {
            log.warn("Invalid password");
            throw new IllegalArgumentException("Invalid password");
        }

        String oldEmail = user.getEmail();

        user.setEmail(newEmail);
        userService.updateEmail(user.getId(), user);

        String message = "Your email has been changed to: " + newEmail
                + "\nIf this wasn’t you, please contact our support team immediately.";

        mailService.send(oldEmail, "Email change notification", message);

        String token = generateJWT(user, user.getRole());
        log.debug("Change JWT={}", token);

        return token;
    }


    /**
     * Generates a random 6-digit numeric verification code.
     *
     * @return 6-digit numeric code as String
     */
    private String generateCode() {
        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange('0', '9')
                .build();
        return generator.generate(PASSWORD_LENGTH);
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
     * Verifies that the plain text password matches the hashed text.
     *
     * @param text plain text
     * @param hashed   hashed
     * @return true if verified
     */
    private boolean verifyHash(final String text, final String hashed) {
        return BCrypt.verifyer().verify(text.toCharArray(), hashed).verified;
    }

    /**
     * Hashes the given plain text using BCrypt.
     *
     * @param text plain text
     * @return hashed string
     */
    private String createHash(final String text) {
        return BCrypt.withDefaults().hashToString(BCRYPT_COST, text.toCharArray());
    }
}
