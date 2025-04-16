package fit.biejk.service;

import fit.biejk.entity.*;
import fit.biejk.repository.OrderProposalRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Service class for managing {@link OrderProposal} entities.
 * <p>
 * Responsible for creating, approving, rejecting, and retrieving proposals.
 * </p>
 */
@Slf4j
@ApplicationScoped
public class OrderProposalService {

    @Inject
    AuthService authService;
    /**
     * Repository for managing order proposal persistence.
     */
    @Inject
    private OrderProposalRepository orderProposalRepository;

    /**
     * Creates a new proposal for an order and assigns it the {@link ProposalStatus#CREATED} status.
     *
     * @param orderProposal the proposal to create
     * @return the newly persisted proposal
     */
    public OrderProposal create(final OrderProposal orderProposal) {
        log.info("Creating new proposal: orderId={}, specialistId={}",
                orderProposal.getOrder().getId(),
                orderProposal.getSpecialist().getId());

        orderProposal.setStatus(ProposalStatus.CREATED);
        orderProposalRepository.persist(orderProposal);

        log.debug("Proposal created with ID={}, status={}", orderProposal.getId(), orderProposal.getStatus());
        return orderProposal;
    }

    /**
     * Approves the selected proposal and rejects all others for the same order.
     *
     * @param order      the order to which the proposals belong
     * @param proposalId the ID of the proposal to approve
     */
    public void approveProposal(final Order order, final Long proposalId) {
        log.info("Approving proposal ID={} for order ID={}", proposalId, order.getId());

        OrderProposal approvedProposal = orderProposalRepository.findById(proposalId);
        if (approvedProposal == null) {
            log.error("Cannot approve proposal: proposal with ID={} not found", proposalId);
            throw new NotFoundException("Proposal with ID=" + proposalId + " not found");
        }

        approvedProposal.setStatus(ProposalStatus.APPROVED);
        orderProposalRepository.persist(approvedProposal);
        log.debug("Proposal ID={} approved", approvedProposal.getId());

        List<OrderProposal> allProposals = getByOrderId(order);
        for (OrderProposal proposal : allProposals) {
            if (!proposal.getId().equals(approvedProposal.getId())) {
                proposal.setStatus(ProposalStatus.REJECTED);
                orderProposalRepository.persist(proposal);
                log.debug("Proposal ID={} rejected", proposal.getId());
            }
        }
    }

    /**
     * Retrieves a single proposal by its ID.
     *
     * @param proposalId the ID of the proposal to retrieve
     * @return the proposal if found
     * @throws NotFoundException if no proposal exists with the given ID
     */
    public OrderProposal getById(final Long proposalId) {
        log.info("Retrieving proposal by ID={}", proposalId);

        OrderProposal proposal = orderProposalRepository.findById(proposalId);
        if (proposal == null) {
            log.error("Proposal with ID={} not found", proposalId);
            throw new NotFoundException("OrderProposal with ID " + proposalId + " not found");
        }

        log.debug("Proposal found: ID={}, status={}", proposal.getId(), proposal.getStatus());
        return proposal;
    }

    /**
     * Retrieves all proposals associated with a specific order.
     *
     * @param order the order for which to fetch proposals
     * @return a list of associated proposals
     */
    public List<OrderProposal> getByOrderId(final Order order) {
        log.info("Fetching all proposals for order ID={}", order.getId());

        List<OrderProposal> proposals = orderProposalRepository.findByOrder(order);
        log.debug("Found {} proposal(s) for order ID={}", proposals.size(), order.getId());

        return proposals;
    }

    /**
     * Returns the specialist associated with the approved proposal for a given order.
     *
     * @param order the order to check
     * @return the approved specialist, or {@code null} if no proposal is approved
     */
    public Specialist getConfirmedSpecialist(final Order order) {
        log.info("Searching for confirmed specialist for order ID={}", order.getId());

        List<OrderProposal> proposals = getByOrderId(order);
        for (OrderProposal proposal : proposals) {
            if (ProposalStatus.APPROVED.equals(proposal.getStatus())) {
                log.debug("Confirmed specialist found: specialistId={}, proposalId={}",
                        proposal.getSpecialist().getId(), proposal.getId());
                return proposal.getSpecialist();
            }
        }

        log.warn("No approved proposal found for order ID={}", order.getId());
        return null;
    }

    public List<OrderProposal> getBySpecialistId(final Long specialistId) {
        log.info("Searching for proposal by specialist ID={}", specialistId);
        if (!authService.isCurrentUser(specialistId)) {
            throw new IllegalArgumentException("Special ist ID " + specialistId + " not authorized");
        }
        return orderProposalRepository.findBySpecialistId(specialistId);
    }

    public Order getOrderById(Long orderProposalId) {
        OrderProposal orderProposal = getById(orderProposalId);
        return orderProposal.getOrder();
    }
}
