package fit.biejk.repository;

import fit.biejk.entity.OrderProposal;
import fit.biejk.entity.ProposalStatus;
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
     * @param page    page number for pagination
     * @param size    number of proposals per page
     * @return a list of {@link OrderProposal} entities related to the given order
     */
    public List<OrderProposal> findByOrderId(final Long orderId, final int page, final int size) {
        return find("order.id", orderId)
                .page(page, size)
                .list();
    }

    /**
     * Retrieves all proposals for a specific order without pagination.
     *
     * @param orderId the ID of the order
     * @return list of all proposals for the order
     */
    public List<OrderProposal> findAllByOrderId(final Long orderId) {
        return find("order.id", orderId)
                .list();
    }

    /**
     * Retrieves all proposals submitted by a specific specialist.
     *
     * @param specialistId the ID of the specialist
     * @param page         page number for pagination
     * @param size         number of proposals per page
     * @return a list of {@link OrderProposal} entities submitted by the given specialist
     */
    public List<OrderProposal> findBySpecialistId(final Long specialistId, final int page, final int size) {
        return find("specialist.id", specialistId)
                .page(page, size)
                .list();
    }

    public void rejectOthersForOrder(Long orderId, Long approvedProposalId) {
        update("status = ?1 where order.id = ?2 and id != ?3",
                ProposalStatus.REJECTED, orderId, approvedProposalId);
    }
}

