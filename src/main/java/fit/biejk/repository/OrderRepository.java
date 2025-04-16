package fit.biejk.repository;

import fit.biejk.entity.Order;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

/**
 * Repository class for performing CRUD operations on {@link Order} entities.
 * <p>
 * Uses {@link PanacheRepository} for simplified data access with Quarkus and Hibernate ORM.
 * Provides custom queries for retrieving orders by client and specialist.
 * </p>
 */
@ApplicationScoped
public class OrderRepository implements PanacheRepository<Order> {

    /**
     * Retrieves all orders created by the specified client.
     *
     * @param clientId the ID of the client
     * @return a list of {@link Order} entities associated with the given client
     */
    public List<Order> findByClientId(final Long clientId) {
        return find("client.id", clientId).list();
    }

    /**
     * Retrieves all orders assigned to the specified specialist.
     *
     * @param specialistId the ID of the specialist
     * @return a list of {@link Order} entities assigned to the given specialist
     */
    public List<Order> findBySpecialistId(final Long specialistId) {
        return find("specialist.id", specialistId).list();
    }
}

