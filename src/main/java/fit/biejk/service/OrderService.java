package fit.biejk.service;

import fit.biejk.entity.Order;
import fit.biejk.entity.OrderProposal;
import fit.biejk.entity.OrderStatus;
import fit.biejk.repository.OrderRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class OrderService {
    @Inject
    OrderRepository orderRepository;

    @Inject
    OrderProposalService orderProposalService;

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

    public Order getById(Long orderId) {
        Order order = orderRepository.findById(orderId);
        if(order == null) {
            throw new NotFoundException("Order not found");
        }
        return order;
    }

    public List<Order> getAll() {
        return orderRepository.listAll();
    }

    public List<OrderProposal> getOrderProposals(Long orderId) {
        Order order = getById(orderId);
        return orderProposalService.getByOrderId(order);
    }

    public OrderProposal getOrderProposalById(Long orderId, Long proposalId) {
        Order order = getById(orderId);
        OrderProposal proposal = orderProposalService.getById(proposalId);
        if (!proposal.getOrder().equals(order)) {
            throw new IllegalArgumentException("Proposal does not belong to this order");
        }
        return proposal;
    }

    @Transactional
    public OrderProposal proposal(Long orderId, OrderProposal proposal) {
        Order order = getById(orderId);
        proposal.setOrder(order);

        orderProposalService.create(proposal);

        order.getOrderProposals().add(proposal);

        order.setStatus(order.getStatus().transitionTo(OrderStatus.CLIENT_PENDING));
        orderRepository.persist(order);
        return proposal;
    }
    @Transactional
    public Order confirm(Long orderId, Long proposalId, int price, LocalDateTime deadline) {
        Order order = getById(orderId);

        order.setPrice(price);
        order.setDeadline(deadline);

        orderProposalService.approveProposal(order, proposalId);

        order.setStatus(order.getStatus().transitionTo(OrderStatus.COMPLETED));
        orderRepository.persist(order);
        return order;
    }

}
