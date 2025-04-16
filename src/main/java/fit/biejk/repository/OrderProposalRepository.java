package fit.biejk.repository;

import fit.biejk.entity.Order;
import fit.biejk.entity.OrderProposal;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

/**
 * Repository class for performing CRUD operations on {@link OrderProposal} entities.
 * <p>
 * Provides custom query methods for accessing proposals related to a specific order.
 * </p>
 */
@ApplicationScoped
public class OrderProposalRepository implements PanacheRepository<OrderProposal> {

    /**
     * Finds all proposals associated with the given order.
     *
     * @param order the order to search proposals for
     * @return list of matching OrderProposal entities
     */
    public List<OrderProposal> findByOrder(final Order order) {
        return find("order", order).list();
    }

    public List<OrderProposal> findBySpecialistId(final Long specialistId) {
        return find("specialist.id", specialistId).list();
    }
}
