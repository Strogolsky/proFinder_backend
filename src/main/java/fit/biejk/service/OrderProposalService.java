package fit.biejk.service;

import fit.biejk.entity.*;
import fit.biejk.repository.OrderProposalRepository;
import fit.biejk.repository.OrderRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class for managing {@link OrderProposal} entities.
 * <p>
 * Responsible for creating, approving, confirming, and retrieving proposals,
 * as well as updating the associated {@link Order} based on proposal status.
 * </p>
 */
@Slf4j
@ApplicationScoped
public class OrderProposalService {

    /**
     * Service for checking the identity of the currently authenticated user.
     */
    @Inject
    private AuthService authService;

    /**
     * Repository for managing order proposal persistence.
     */
    @Inject
    private OrderProposalRepository orderProposalRepository;

    /**
     * Repository for managing order persistence.
     */
    @Inject
    private OrderRepository orderRepository;

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
     * Approves a specific proposal and rejects all other proposals for the same order.
     *
     * @param orderId    the ID of the order
     * @param proposalId the ID of the proposal to approve
     * @throws NotFoundException if the proposal does not exist
     */
    public void approveProposal(final Long orderId, final Long proposalId) {
        log.info("Approving proposal ID={} for order ID={}", proposalId, orderId);

        OrderProposal approvedProposal = orderProposalRepository.findById(proposalId);
        if (approvedProposal == null) {
            log.error("Cannot approve proposal: proposal with ID={} not found", proposalId);
            throw new NotFoundException("Proposal with ID=" + proposalId + " not found");
        }

        approvedProposal.setStatus(ProposalStatus.APPROVED);
        orderProposalRepository.persist(approvedProposal);
        log.debug("Proposal ID={} approved", approvedProposal.getId());

        List<OrderProposal> allProposals = getByOrderId(orderId);
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
     * @param orderId the ID of the order
     * @return list of proposals for the order
     */
    public List<OrderProposal> getByOrderId(final Long orderId) {
        log.info("Fetching all proposals for order ID={}", orderId);

        List<OrderProposal> proposals = orderProposalRepository.findByOrderId(orderId);
        log.debug("Found {} proposal(s) for order ID={}", proposals.size(), orderId);

        return proposals;
    }

    /**
     * Returns the specialist whose proposal has been approved for a given order.
     *
     * @param orderId the ID of the order
     * @return the approved specialist, or {@code null} if no approved proposal is found
     */
    public Specialist getConfirmedSpecialist(final Long orderId) {
        log.info("Searching for confirmed specialist for order ID={}", orderId);

        List<OrderProposal> proposals = getByOrderId(orderId);
        for (OrderProposal proposal : proposals) {
            if (ProposalStatus.APPROVED.equals(proposal.getStatus())) {
                log.debug("Confirmed specialist found: specialistId={}, proposalId={}",
                        proposal.getSpecialist().getId(), proposal.getId());
                return proposal.getSpecialist();
            }
        }

        log.warn("No approved proposal found for order ID={}", orderId);
        return null;
    }

    /**
     * Retrieves all proposals submitted by a specific specialist.
     * <p>
     * The caller must be the same as the specialist whose ID is provided.
     * </p>
     *
     * @param specialistId the ID of the specialist
     * @return list of proposals submitted by the specialist
     * @throws IllegalArgumentException if the caller is not the same as the specialist
     */
    public List<OrderProposal> getBySpecialistId(final Long specialistId) {
        log.info("Searching for proposal by specialist ID={}", specialistId);
        if (!authService.isCurrentUser(specialistId)) {
            throw new IllegalArgumentException("Specialist ID " + specialistId + " not authorized");
        }
        return orderProposalRepository.findBySpecialistId(specialistId);
    }

    /**
     * Retrieves the order associated with a given proposal.
     *
     * @param orderProposalId the ID of the proposal
     * @return the related order
     */
    public Order getOrderByProposalId(final Long orderProposalId) {
        OrderProposal orderProposal = getById(orderProposalId);
        return orderProposal.getOrder();
    }
}
