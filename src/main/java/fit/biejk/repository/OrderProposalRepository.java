package fit.biejk.repository;

import fit.biejk.entity.OrderProposal;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

/**
 * Repository class for performing CRUD operations on {@link OrderProposal} entities.
 * <p>
 * Provides custom query methods for accessing proposals by order ID and specialist ID.
 * Uses Quarkus Panache to simplify database interactions.
 * </p>
 */
@ApplicationScoped
public class OrderProposalRepository implements PanacheRepository<OrderProposal> {

    /**
     * Retrieves all proposals associated with a specific order.
     *
     * @param orderId the ID of the order
     * @return a list of {@link OrderProposal} entities related to the given order
     */
    public List<OrderProposal> findByOrderId(final Long orderId) {
        return find("order.id", orderId).list();
    }

    /**
     * Retrieves all proposals submitted by a specific specialist.
     *
     * @param specialistId the ID of the specialist
     * @return a list of {@link OrderProposal} entities submitted by the given specialist
     */
    public List<OrderProposal> findBySpecialistId(final Long specialistId) {
        return find("specialist.id", specialistId).list();
    }
}

