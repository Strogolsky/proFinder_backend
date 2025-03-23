package fit.biejk.repository;

import fit.biejk.entity.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

/**
 * Repository class for performing CRUD operations on {@link User} entities.
 * <p>
 * Includes custom method to find a user by their email address.
 * </p>
 */
@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    /**
     * Finds a user by their email.
     *
     * @param email the email address to search for
     * @return an {@link Optional} containing the User if found, or empty if not
     */
    public Optional<User> findByEmail(final String email) {
        return find("email", email).firstResultOptional();
    }
}
