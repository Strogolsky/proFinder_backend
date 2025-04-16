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


    public List<OrderProposal> getByOrderId(final Long orderId) {
        log.info("Fetching all proposals for order ID={}", orderId);

        List<OrderProposal> proposals = orderProposalRepository.findByOrderId(orderId);
        log.debug("Found {} proposal(s) for order ID={}", proposals.size(), orderId);

        return proposals;
    }

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

    @Transactional
    public Order confirm(final Long proposalId, final int price, final LocalDateTime deadline) {
        log.info("Confirm proposal: proposalId={}", proposalId);
        Order order = getOrderById(proposalId);
        if (!authService.isCurrentUser(order.getClient().getId())) {
            log.error("User is not the owner of this order. orderId={}, clientId={}",
                    order.getId(), order.getClient().getId());
            throw new IllegalArgumentException();
        }
        order.setPrice(price);
        order.setDeadline(deadline);
        approveProposal(order.getId(), proposalId);
        order.setStatus(order.getStatus().transitionTo(OrderStatus.COMPLETED));
        orderRepository.persist(order);
        log.debug("Order confirmed with ID={}", order.getId());
        return order;
    }
}
