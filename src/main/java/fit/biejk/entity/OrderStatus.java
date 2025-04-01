package fit.biejk.entity;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Enumeration representing possible statuses of an order.
 * <p>
 * Defines allowed transitions between different statuses.
 * </p>
 */
@Slf4j
public enum OrderStatus {

    /**
     * Initial status after an order is created.
     */
    CREATED,

    /**
     * Status indicating the order has been cancelled.
     */
    CANCELLED,

    /**
     * Status indicating the order is awaiting client actions.
     */
    CLIENT_PENDING,

    /**
     * Status indicating the order has been completed successfully.
     */
    COMPLETED,

    REVIEWED;

    /**
     * Map defining valid transitions from each status to other statuses.
     */
    private static final Map<OrderStatus, Set<OrderStatus>> VALID_TRANSITIONS = new HashMap<>();

    static {
        VALID_TRANSITIONS.put(CREATED, Set.of(CANCELLED, CLIENT_PENDING));
        VALID_TRANSITIONS.put(CLIENT_PENDING, Set.of(CLIENT_PENDING, CANCELLED, COMPLETED));
        VALID_TRANSITIONS.put(COMPLETED, Set.of(REVIEWED));
        VALID_TRANSITIONS.put(REVIEWED, Set.of());
        VALID_TRANSITIONS.put(CANCELLED, Set.of());
    }

    /**
     * Checks if transition to the given status is valid.
     *
     * @param newStatus The status to transition to.
     * @return True if the transition is valid, otherwise false.
     */
    public boolean isValidTransition(final OrderStatus newStatus) {
        log.debug("Check if transition from {} to {} is valid", this, newStatus);
        return VALID_TRANSITIONS.getOrDefault(this, Set.of()).contains(newStatus);
    }

    /**
     * Retrieves the initial order status.
     *
     * @return Initial status {@link #CREATED}.
     */
    public static OrderStatus getStartOrder() {
        return CREATED;
    }

    /**
     * Attempts to transition to the specified new status.
     *
     * @param newStatus The status to transition to.
     * @return The new status if the transition is valid.
     * @throws IllegalStateException if the transition is invalid.
     */
    public OrderStatus transitionTo(final OrderStatus newStatus) {
        log.info("Transitioning order from {} to {}", this, newStatus);
        if (!isValidTransition(newStatus)) {
            log.error("Invalid transition from {} to {}", this, newStatus);
            throw new IllegalStateException("Cannot transitionTo from " + this + " to " + newStatus);
        }
        log.debug("Valid transition from {} to {}", this, newStatus);
        return newStatus;
    }
}
