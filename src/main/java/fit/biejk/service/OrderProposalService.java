package fit.biejk.service;

import fit.biejk.entity.Order;
import fit.biejk.entity.OrderProposal;
import fit.biejk.entity.ProposalStatus;
import fit.biejk.repository.OrderProposalRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Service class for managing {@link OrderProposal} entities.
 * <p>
 * Handles creation, retrieval, approval, and rejection of proposals related to orders.
 * </p>
 */
@Slf4j
@ApplicationScoped
public class OrderProposalService {

    /**
     * Repository for accessing and persisting order proposals.
     */
    @Inject
    private OrderProposalRepository orderProposalRepository;

    /**
     * Creates a new order proposal and sets its initial status to {@link ProposalStatus#CREATED}.
     *
     * @param orderProposal proposal to create
     * @return created proposal
     */
    public OrderProposal create(final OrderProposal orderProposal) {
        log.info("Create orderProposal for orderId={}, specialistId={}",
                orderProposal.getOrder().getId(),
                orderProposal.getSpecialist().getId());
        orderProposal.setStatus(ProposalStatus.CREATED);
        orderProposalRepository.persist(orderProposal);
        log.debug("OrderProposal created with ID={}", orderProposal.getId());
        return orderProposal;
    }

    /**
     * Approves a specific proposal and rejects all others for the same order.
     *
     * @param order      order the proposal belongs to
     * @param proposalId ID of the proposal to approve
     */
    public void approveProposal(final Order order, final Long proposalId) {
        log.info("Approve proposal: orderId={}, proposalId={}", order.getId(), proposalId);
        OrderProposal approvedProposal = orderProposalRepository.findById(proposalId);
        approvedProposal.setStatus(ProposalStatus.APPROVED);
        orderProposalRepository.persist(approvedProposal);

        log.debug("Approved proposal with ID={}", approvedProposal.getId());
        List<OrderProposal> allProposals = getByOrderId(order);

        for (OrderProposal proposal : allProposals) {
            if (!proposal.getId().equals(approvedProposal.getId())) {
                proposal.setStatus(ProposalStatus.REJECTED);
                orderProposalRepository.persist(proposal);
                log.debug("Rejected proposal with ID={}", proposal.getId());
            }
        }
    }

    /**
     * Retrieves a proposal by its ID.
     *
     * @param proposalId ID of the proposal
     * @return found proposal
     * @throws NotFoundException if no proposal with the given ID exists
     */
    public OrderProposal getById(final Long proposalId) {
        log.info("Get proposal by ID={}", proposalId);
        OrderProposal result = orderProposalRepository.findById(proposalId);

        if (result == null) {
            log.error("OrderProposal with ID={} not found", proposalId);
            throw new NotFoundException("OrderProposal with id " + proposalId + " not found");
        }
        log.debug("Found proposal with ID={}", result.getId());

        return result;
    }

    /**
     * Retrieves all proposals for the given order.
     *
     * @param order the order
     * @return list of proposals associated with the order
     */
    public List<OrderProposal> getByOrderId(final Order order) {
        log.info("Get proposals for order ID={}", order.getId());
        List<OrderProposal> proposals = orderProposalRepository.findByOrder(order);
        log.debug("Found {} proposals for order ID={}", proposals.size(), order.getId());
        return proposals;
    }
}
