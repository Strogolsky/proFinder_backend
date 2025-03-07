package fit.biejk.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public enum OrderStatus {
    CREATED,
    CANCELLED,
    CLIENT_PENDING,
    COMPLETED;

    private static final Map<OrderStatus, Set<OrderStatus>> validTransitions = new HashMap<>();

    static {
        validTransitions.put(CREATED,Set.of(CANCELLED, CLIENT_PENDING));

        validTransitions.put(CLIENT_PENDING, Set.of(CLIENT_PENDING, CANCELLED, COMPLETED));

        validTransitions.put(COMPLETED, Set.of());

        validTransitions.put(CANCELLED,Set.of());
    }

    public boolean isValidTransition(OrderStatus newStatus) {
        return validTransitions.getOrDefault(this,Set.of()).contains(newStatus);
    }

    public static OrderStatus getStartOrder() {
        return OrderStatus.CREATED;
    }

    public OrderStatus transitionTo(OrderStatus newStatus) {
        if (!isValidTransition(newStatus)) {
            throw new IllegalStateException("Cannot transitionTo from " + this + " to " + newStatus);
        }
        return newStatus;
    }
}
