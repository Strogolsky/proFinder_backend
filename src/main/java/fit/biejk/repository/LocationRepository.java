package fit.biejk.repository;

import fit.biejk.entity.Location;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Repository class for accessing {@link Location} entities in the database.
 * <p>
 * Provides custom queries and inherits standard CRUD operations
 * from {@link PanacheRepository}.
 * </p>
 */
@ApplicationScoped
public class LocationRepository implements PanacheRepository<Location> {

    /**
     * Finds a {@link Location} entity by its unique name.
     *
     * @param name the name of the location to search for
     * @return the matching {@link Location} entity, or {@code null} if not found
     */
    public Location findByName(final String name) {
        return find("name", name).firstResult();
    }
}
