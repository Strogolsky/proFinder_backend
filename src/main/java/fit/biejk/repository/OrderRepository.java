package fit.biejk.repository;

import fit.biejk.entity.Order;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

/**
 * Repository class for performing CRUD operations on {@link Order} entities.
 * <p>
 * Uses PanacheRepository for simplified data access in Quarkus.
 * </p>
 */
@ApplicationScoped
public class OrderRepository implements PanacheRepository<Order> {
    public List<Order> findByClientId(Long clientId) {
        return find("client.id", clientId).list();
    }

    public List<Order> findBySpecialistId(Long specialistId) {
        return find("specialist.id", specialistId).list();
    }

}
