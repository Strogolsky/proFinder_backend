package fit.biejk.service;

import fit.biejk.entity.Order;
import fit.biejk.entity.OrderStatus;
import fit.biejk.repository.OrderRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class OrderService {
    @Inject
    OrderRepository orderRepository;

    @Transactional
    public Order create(Order order) {
        order.setStatus(OrderStatus.getStartOrder());
        orderRepository.persist(order);
        return order;
    }

    @Transactional
    public Order cancel(Long orderId) {
        Order order = orderRepository.findById(orderId);
        if(order == null) {
            throw new NotFoundException("Order not found");
        }
        order.setStatus(order.getStatus().transitionTo(OrderStatus.CANCELLED));
        orderRepository.persist(order);
        return order;
    }
}
