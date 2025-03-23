package fit.biejk.service;

import fit.biejk.entity.Order;
import fit.biejk.entity.OrderProposal;
import fit.biejk.entity.OrderStatus;
import fit.biejk.repository.OrderRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class for managing {@link Order} entities.
 * <p>
 * Handles business logic for creating, updating, deleting, and confirming orders,
 * as well as working with related {@link OrderProposal} objects.
 * </p>
 */
@Slf4j
@ApplicationScoped
public class OrderService {

    /**
     * Repository for accessing order data.
     */
    @Inject
    private OrderRepository orderRepository;

    /**
     * Service for managing order proposals.
     */
    @Inject
    private OrderProposalService orderProposalService;

    /**
     * Service for user identity validation.
     */
    @Inject
    private AuthService authService;

    /**
     * Creates a new order with default status.
     *
     * @param order order entity
     * @return created order
     */
    @Transactional
    public Order create(final Order order) {
        log.info("Create order: clientId={}, description={}", order.getClient().getId(), order.getDescription());
        order.setStatus(OrderStatus.getStartOrder());
        orderRepository.persist(order);
        log.debug("Order created with ID={}", order.getId());
        return order;
    }

    /**
     * Updates the fields of an existing order.
     *
     * @param orderId ID of the order to update
     * @param order   new order data
     * @return original order (before update)
     */
    @Transactional
    public Order update(final Long orderId, final Order order) {
        log.info("Update order: orderId={}, newDescription={}", orderId, order.getDescription());
        Order old = getById(orderId);
        if (!authService.isCurrentUser(old.getClient().getId())) {
            log.error("User is not the owner of this order. orderId={}, clientId={}", orderId, old.getClient().getId());
            throw new IllegalArgumentException();
        }
        old.setDescription(order.getDescription());
        old.setPrice(order.getPrice());
        old.setDeadline(order.getDeadline());
        log.debug("Order updated with ID={}", orderId);
        return order;
    }

    /**
     * Deletes an order by ID.
     *
     * @param orderId ID of the order
     */
    @Transactional
    public void delete(final Long orderId) {
        log.info("Delete order: orderId={}", orderId);
        Order order = getById(orderId);
        if (!authService.isCurrentUser(order.getClient().getId())) {
            log.error("User is not the owner of this order. orderId={}, clientId={}",
                    orderId, order.getClient().getId());
            throw new IllegalArgumentException();
        }
        orderRepository.delete(order);
        log.debug("Order deleted with ID={}", orderId);
    }

    /**
     * Cancels an order, transitioning its status to CANCELLED.
     *
     * @param orderId ID of the order
     * @return updated order
     */
    @Transactional
    public Order cancel(final Long orderId) {
        log.info("Cancel order: orderId={}", orderId);
        Order order = getById(orderId);
        if (!authService.isCurrentUser(order.getClient().getId())) {
            log.error("User is not the owner of this order. orderId={}, clientId={}",
                    orderId, order.getClient().getId());
            throw new IllegalArgumentException();
        }
        order.setStatus(order.getStatus().transitionTo(OrderStatus.CANCELLED));
        orderRepository.persist(order);
        log.debug("Order canceled with ID={}", orderId);
        return order;
    }

    /**
     * Gets an order by ID.
     *
     * @param orderId ID of the order
     * @return found order
     * @throws NotFoundException if not found
     */
    public Order getById(final Long orderId) {
        log.info("Get order by ID={}", orderId);
        Order order = orderRepository.findById(orderId);
        if (order == null) {
            log.error("Order with ID={} not found", orderId);
            throw new NotFoundException("Order not found");
        }
        return order;
    }

    /**
     * Retrieves all orders.
     *
     * @return list of orders
     */
    public List<Order> getAll() {
        log.info("Get all orders");
        List<Order> orders = orderRepository.listAll();
        log.debug("Found {} orders", orders.size());
        return orders;
    }

    /**
     * Gets all proposals for a given order.
     *
     * @param orderId ID of the order
     * @return list of proposals
     */
    public List<OrderProposal> getOrderProposals(final Long orderId) {
        log.info("Get proposals for orderId={}", orderId);
        Order order = getById(orderId);
        List<OrderProposal> proposals = orderProposalService.getByOrderId(order);
        log.debug("Found {} proposals for orderId={}", proposals.size(), orderId);
        return proposals;
    }

    /**
     * Gets a proposal by ID and checks it belongs to the given order.
     *
     * @param orderId    order ID
     * @param proposalId proposal ID
     * @return found proposal
     */
    public OrderProposal getOrderProposalById(final Long orderId, final Long proposalId) {
        log.info("Get proposal by ID={}, orderId={}", proposalId, orderId);
        Order order = getById(orderId);
        OrderProposal proposal = orderProposalService.getById(proposalId);
        if (!proposal.getOrder().equals(order)) {
            log.error("Proposal does not belong to this order. orderId={}, proposalId={}", orderId, proposalId);
            throw new IllegalArgumentException("Proposal does not belong to this order");
        }
        return proposal;
    }

    /**
     * Adds a new proposal to an order.
     *
     * @param orderId  ID of the order
     * @param proposal proposal entity
     * @return created proposal
     */
    @Transactional
    public OrderProposal proposal(final Long orderId, final OrderProposal proposal) {
        log.info("Create proposal: orderId={}, specialistId={}", orderId, proposal.getSpecialist().getId());
        Order order = getById(orderId);
        if (haveSpecialistProposal(order, proposal)) {
            log.error("Order proposal already exists for this specialist. orderId={}, specialistId={}",
                    orderId, proposal.getSpecialist().getId());
            throw new IllegalArgumentException("Order proposal already exists");
        }
        proposal.setOrder(order);
        orderProposalService.create(proposal);
        order.getOrderProposals().add(proposal);
        order.setStatus(order.getStatus().transitionTo(OrderStatus.CLIENT_PENDING));
        orderRepository.persist(order);
        log.debug("Proposal created with ID={}", proposal.getId());
        return proposal;
    }

    /**
     * Confirms a proposal, updates price and deadline, and completes the order.
     *
     * @param orderId    ID of the order
     * @param proposalId ID of the approved proposal
     * @param price      final agreed price
     * @param deadline   final deadline
     * @return updated order
     */
    @Transactional
    public Order confirm(final Long orderId, final Long proposalId, final int price, final LocalDateTime deadline) {
        log.info("Confirm proposal: orderId={}, proposalId={}", orderId, proposalId);
        Order order = getById(orderId);
        if (!authService.isCurrentUser(order.getClient().getId())) {
            log.error("User is not the owner of this order. orderId={}, clientId={}",
                    orderId, order.getClient().getId());
            throw new IllegalArgumentException();
        }
        order.setPrice(price);
        order.setDeadline(deadline);
        orderProposalService.approveProposal(order, proposalId);
        order.setStatus(order.getStatus().transitionTo(OrderStatus.COMPLETED));
        orderRepository.persist(order);
        log.debug("Order confirmed with ID={}", orderId);
        return order;
    }

    /**
     * Checks if the given specialist already has a proposal for the order.
     *
     * @param order    the order
     * @param proposal the proposal to check
     * @return true if proposal already exists, false otherwise
     */
    public boolean haveSpecialistProposal(final Order order, final OrderProposal proposal) {
        log.info("Check if specialist proposal already exists: orderId={}, specialistId={}",
                order.getId(), proposal.getSpecialist().getId());
        List<OrderProposal> orderProposals = order.getOrderProposals();
        for (OrderProposal orderProposal : orderProposals) {
            if (orderProposal.getSpecialist().getId().equals(proposal.getSpecialist().getId())) {
                log.debug("Proposal already exists for specialistId={}", proposal.getSpecialist().getId());
                return true;
            }
        }
        return false;
    }
}
