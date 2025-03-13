package fit.biejk.service;

import fit.biejk.entity.Client;
import fit.biejk.entity.User;
import fit.biejk.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.NoArgsConstructor;
import org.jose4j.jwk.Use;

import java.util.Optional;

@ApplicationScoped
@NoArgsConstructor
public class UserService {
    @Inject
    UserRepository userRepository;

    public void checkUniqueEmail(String email) {
        if(userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("User " + email + " already exists");
        }
    }

    public User getByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isEmpty()) {
            throw new NotFoundException("User " + email + " not found");
        }
        return user.get();
    }

    @Transactional
    public User update(Long userId, User newUser) {
        User existingUser = userRepository.findById(userId);

        if (existingUser == null) {
            throw new NotFoundException("User with id " + userId + " not found");
        }

        existingUser.setPhoneNumber(newUser.getPhoneNumber());
        existingUser.setLocation(newUser.getLocation());
        existingUser.setFirstName(newUser.getFirstName());
        existingUser.setLastName(newUser.getLastName());

        return existingUser;
    }

    @Transactional
    public void delete(Long userId) {
        User existingUser = userRepository.findById(userId);
        if (existingUser == null) {
            throw new NotFoundException("User with id " + userId + " not found");
        }
        userRepository.delete(existingUser);
    }
}
