package fit.biejk.service;

import fit.biejk.entity.*;
import fit.biejk.repository.OrderRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;

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
     * Service layer for handling client-related business logic.
     */
    @Inject
    private ClientService clientService;

    /** Service for accessing client reviews. */
    @Inject
    private ReviewService reviewService;

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
        // todo maybe add update location
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
    /**
     * Creates a review for a completed order.
     * <p>
     * The review can only be created by the client who owns the order,
     * and only if the order status is {@link OrderStatus#COMPLETED}.
     * </p>
     *
     * @param orderId ID of the order to review
     * @param review  review to be created
     * @return saved review
     * @throws IllegalArgumentException if the order is not completed,
     *                                  user is not logged in,
     *                                  or not the owner of the order
     */
    @Transactional
    public Review review(final Long orderId, final Review review) {
        log.info("Creating review for orderId={}", orderId);

        Order order = getById(orderId);

        if (order.getStatus() != OrderStatus.COMPLETED) {
            log.error("Cannot review: order not completed. orderId={}, status={}", orderId, order.getStatus());
            throw new IllegalArgumentException("Order is not completed");
        }

        Long clientId = authService.getCurrentUserId();
        if (clientId == null) {
            log.error("Cannot review: client is not authenticated");
            throw new IllegalArgumentException("User is not logged in");
        }

        if (!clientId.equals(order.getClient().getId())) {
            log.error("Cannot review: clientId={} is not the owner of orderId={}", clientId, orderId);
            throw new IllegalArgumentException("Client is not the owner of this order");
        }

        Client client = clientService.getById(clientId);
        Specialist specialist = orderProposalService.getConfirmedSpecialist(orderId);
        if (specialist == null) {
            log.error("Cannot review: no approved specialist found for orderId={}", orderId);
            throw new IllegalArgumentException("No specialist found for this order");
        }

        order.setStatus(order.getStatus().transitionTo(OrderStatus.REVIEWED));

        review.setClient(client);
        review.setSpecialist(specialist);
        review.setOrder(order);

        Review saved = reviewService.create(review);

        log.debug("Review created: reviewId={}, rating={}, orderId={}", saved.getId(), saved.getRating(), orderId);

        return saved;
    }

    /**
     * Retrieves all orders created by the specified client.
     * <p>
     * Ensures that the requesting user is the same as the target client.
     * </p>
     *
     * @param userId ID of the client
     * @return list of orders belonging to the client
     * @throws IllegalArgumentException if the current user is not the same as the client
     */
    public List<Order> getByClientId(final Long userId) {
        log.info("Get orders by clientId={}", userId);
        if (!authService.isCurrentUser(userId)) {
            log.warn("User is not logged in");
            throw new IllegalArgumentException("User is not logged in");
        } // todo delete, when admin system will be created
        return orderRepository.findByClientId(userId);
    }

    /**
     * Retrieves all orders assigned to the specified specialist.
     * <p>
     * Ensures that the requesting user is the same as the target specialist.
     * </p>
     *
     * @param specialistId ID of the specialist
     * @return list of orders currently assigned to the specialist
     * @throws IllegalArgumentException if the current user is not the same as the specialist
     */
    public List<Order> getBySpecialistId(final Long specialistId) {
        log.info("Get orders by specialistId={}", specialistId);
        if (!authService.isCurrentUser(specialistId)) {
            log.warn("User is not logged in");
            throw new IllegalArgumentException("User is not logged in");
        }
        return orderRepository.findBySpecialistId(specialistId);
    }

}
