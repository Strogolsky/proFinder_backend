package fit.biejk.service;

import fit.biejk.entity.User;
import fit.biejk.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.NoArgsConstructor;

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
