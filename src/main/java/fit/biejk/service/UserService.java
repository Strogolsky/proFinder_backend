package fit.biejk.service;

import fit.biejk.entity.User;
import fit.biejk.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
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
    public void updateCommonFields(User existingUser, User newUser) {
        existingUser.setPhoneNumber(newUser.getPhoneNumber());
        existingUser.setLocation(newUser.getLocation());
        existingUser.setFirstName(newUser.getFirstName());
        existingUser.setLastName(newUser.getLastName());
    }
}
