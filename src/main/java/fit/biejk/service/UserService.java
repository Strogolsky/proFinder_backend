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

@Slf4j
@NoArgsConstructor
@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;

    public void checkUniqueEmail(String email) {
        log.info("Check unique email: {}", email);
        if (userRepository.findByEmail(email).isPresent()) {
            log.error("User {} already exists", email);
            throw new IllegalArgumentException("User " + email + " already exists");
        }
    }

    public User getByEmail(String email) {
        log.info("Get user by email={}", email);
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            log.error("User {} not found", email);
            throw new NotFoundException("User " + email + " not found");
        }
        log.debug("Found user with ID={}", user.get().getId());
        return user.get();
    }

    @Transactional
    public User update(Long userId, User newUser) {
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

    @Transactional
    public void delete(Long userId) {
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
