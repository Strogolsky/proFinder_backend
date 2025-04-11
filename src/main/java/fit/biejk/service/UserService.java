package fit.biejk.service;

import fit.biejk.entity.User;
import fit.biejk.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * Service class for managing {@link User} entities.
 * <p>
 * Handles shared user operations such as email uniqueness check,
 * updating profile data, and deleting users.
 * </p>
 */
@Slf4j
@NoArgsConstructor
@ApplicationScoped
public class UserService {

    /**
     * Repository for accessing user data.
     */
    @Inject
    private UserRepository userRepository;

    /**
     * Checks if the given email is unique.
     *
     * @param email email to check
     * @throws IllegalArgumentException if the email is already taken
     */
    public void checkUniqueEmail(final String email) {
        log.info("Check unique email: {}", email);
        if (userRepository.findByEmail(email).isPresent()) {
            log.error("User {} already exists", email);
            throw new IllegalArgumentException("User " + email + " already exists");
        }
    }

    /**
     * Retrieves a user by their email.
     *
     * @param email email of the user
     * @return found user
     * @throws NotFoundException if user is not found
     */
    public User getByEmail(final String email) {
        log.info("Get user by email={}", email);
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            log.error("User {} not found", email);
            throw new NotFoundException("User " + email + " not found");
        }
        log.debug("Found user with ID={}", user.get().getId());
        return user.get();
    }

    /**
     * Retrieves a user by their ID.
     *
     * Logs the operation, throws NotFoundException if the user does not exist.
     *
     * @param id the ID of the user to retrieve
     * @return the found User entity
     * @throws NotFoundException if no user is found with the given ID
     */
    public User getById(final Long id) {
        log.info("Get user by id={}", id);
        User user = userRepository.findById(id);
        if (user == null) {
            log.error("User {} not found", id);
            throw new NotFoundException("User " + id + " not found");
        }
        log.debug("Found user with ID={}", user.getId());
        return user;
    }

    /**
     * Checks if a user exists by their ID.
     *
     * @param id the ID of the user
     * @return true if the user exists, false otherwise
     */
    public boolean existById(final Long id) {
        User user = userRepository.findById(id);
        if (user == null) {
            log.warn("User {} not found", id);
            return false;
        }
        log.info("User {} exists", id);
        return true;
    }

    /**
     * Updates the user data by ID.
     *
     * @param userId   ID of the user
     * @param newUser  updated user data
     * @return updated user
     * @throws NotFoundException if user is not found
     */
    @Transactional
    public User update(final Long userId, final User newUser) {
        log.info("Update user: userId={}, newEmail={}", userId, newUser.getEmail());
        User existingUser = userRepository.findById(userId);
        if (existingUser == null) {
            log.error("User with ID={} not found", userId);
            throw new NotFoundException("User with id " + userId + " not found");
        }
        existingUser.setPhoneNumber(newUser.getPhoneNumber());
        existingUser.setLocation(newUser.getLocation());
        existingUser.setFirstName(newUser.getFirstName());
        existingUser.setLastName(newUser.getLastName());
        log.debug("User updated with ID={}", existingUser.getId());
        return existingUser;
    }

    /**
     * Deletes the user by ID.
     *
     * @param userId ID of the user
     * @throws NotFoundException if user is not found
     */
    @Transactional
    public void delete(final Long userId) {
        log.info("Delete user: userId={}", userId);
        User existingUser = userRepository.findById(userId);
        if (existingUser == null) {
            log.error("User with ID={} not found", userId);
            throw new NotFoundException("User with id " + userId + " not found");
        }
        userRepository.delete(existingUser);
        log.debug("User deleted with ID={}", userId);
    }
}
