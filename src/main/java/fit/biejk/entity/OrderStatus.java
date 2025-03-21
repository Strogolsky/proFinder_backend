package fit.biejk.entity;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
public enum OrderStatus {

    CREATED,
    CANCELLED,
    CLIENT_PENDING,
    COMPLETED;

    private static final Map<OrderStatus, Set<OrderStatus>> validTransitions = new HashMap<>();

    static {
        validTransitions.put(CREATED, Set.of(CANCELLED, CLIENT_PENDING));
        validTransitions.put(CLIENT_PENDING, Set.of(CLIENT_PENDING, CANCELLED, COMPLETED));
        validTransitions.put(COMPLETED, Set.of());
        validTransitions.put(CANCELLED, Set.of());
    }

    public boolean isValidTransition(OrderStatus newStatus) {
        log.debug("Check if transition from {} to {} is valid", this, newStatus);
        return validTransitions.getOrDefault(this, Set.of()).contains(newStatus);
    }

    public static OrderStatus getStartOrder() {
        return CREATED;
    }

    public OrderStatus transitionTo(OrderStatus newStatus) {
        log.info("Transitioning order from {} to {}", this, newStatus);
        if (!isValidTransition(newStatus)) {
            log.error("Invalid transition from {} to {}", this, newStatus);
            throw new IllegalStateException("Cannot transitionTo from " + this + " to " + newStatus);
        }
        log.debug("Valid transition from {} to {}", this, newStatus);
        return newStatus;
    }
}
