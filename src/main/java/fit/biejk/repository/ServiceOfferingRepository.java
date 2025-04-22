package fit.biejk.repository;

import fit.biejk.entity.ServiceOffering;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Repository for managing {@link ServiceOffering} entities.
 * <p>
 * Provides CRUD operations using Panache.
 * </p>
 */
@ApplicationScoped
public class ServiceOfferingRepository implements PanacheRepository<ServiceOffering> {
}
