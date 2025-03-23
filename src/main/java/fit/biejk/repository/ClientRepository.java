package fit.biejk.repository;

import fit.biejk.entity.Client;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Repository class for performing CRUD operations on {@link Client} entities.
 * <p>
 * Uses PanacheRepository for simplified data access in Quarkus.
 * </p>
 */
@ApplicationScoped
public class ClientRepository implements PanacheRepository<Client> {
}
