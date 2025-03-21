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

@Slf4j
@ApplicationScoped
public class OrderService {

    @Inject
    OrderRepository orderRepository;

    @Inject
    OrderProposalService orderProposalService;

    @Inject
    AuthService authService;

    @Transactional
    public Order create(Order order) {
        log.info("Create order: clientId={}, description={}", order.getClient().getId(), order.getDescription());
        order.setStatus(OrderStatus.getStartOrder());
        orderRepository.persist(order);
        log.debug("Order created with ID={}", order.getId());
        return order;
    }

    @Transactional
    public Order update(Long orderId, Order order) {
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

    @Transactional
    public void delete(Long orderId) {
        log.info("Delete order: orderId={}", orderId);
        Order order = getById(orderId);
        if (!authService.isCurrentUser(order.getClient().getId())) {
            log.error("User is not the owner of this order. orderId={}, clientId={}", orderId, order.getClient().getId());
            throw new IllegalArgumentException();
        }
        orderRepository.delete(order);
        log.debug("Order deleted with ID={}", orderId);
    }

    @Transactional
    public Order cancel(Long orderId) {
        log.info("Cancel order: orderId={}", orderId);
        Order order = getById(orderId);
        if (!authService.isCurrentUser(order.getClient().getId())) {
            log.error("User is not the owner of this order. orderId={}, clientId={}", orderId, order.getClient().getId());
            throw new IllegalArgumentException();
        }
        order.setStatus(order.getStatus().transitionTo(OrderStatus.CANCELLED));
        orderRepository.persist(order);
        log.debug("Order canceled with ID={}", orderId);
        return order;
    }

    public Order getById(Long orderId) {
        log.info("Get order by ID={}", orderId);
        Order order = orderRepository.findById(orderId);
        if (order == null) {
            log.error("Order with ID={} not found", orderId);
            throw new NotFoundException("Order not found");
        }
        return order;
    }

    public List<Order> getAll() {
        log.info("Get all orders");
        List<Order> orders = orderRepository.listAll();
        log.debug("Found {} orders", orders.size());
        return orders;
    }

    public List<OrderProposal> getOrderProposals(Long orderId) {
        log.info("Get proposals for orderId={}", orderId);
        Order order = getById(orderId);
        List<OrderProposal> proposals = orderProposalService.getByOrderId(order);
        log.debug("Found {} proposals for orderId={}", proposals.size(), orderId);
        return proposals;
    }

    public OrderProposal getOrderProposalById(Long orderId, Long proposalId) {
        log.info("Get proposal by ID={}, orderId={}", proposalId, orderId);
        Order order = getById(orderId);
        OrderProposal proposal = orderProposalService.getById(proposalId);
        if (!proposal.getOrder().equals(order)) {
            log.error("Proposal does not belong to this order. orderId={}, proposalId={}", orderId, proposalId);
            throw new IllegalArgumentException("Proposal does not belong to this order");
        }
        return proposal;
    }

    @Transactional
    public OrderProposal proposal(Long orderId, OrderProposal proposal) {
        log.info("Create proposal: orderId={}, specialistId={}", orderId, proposal.getSpecialist().getId());
        Order order = getById(orderId);
        if (haveSpecialistProposal(order, proposal)) {
            log.error("Order proposal already exists for this specialist. orderId={}, specialistId={}", orderId, proposal.getSpecialist().getId());
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

    @Transactional
    public Order confirm(Long orderId, Long proposalId, int price, LocalDateTime deadline) {
        log.info("Confirm proposal: orderId={}, proposalId={}", orderId, proposalId);
        Order order = getById(orderId);
        if (!authService.isCurrentUser(order.getClient().getId())) {
            log.error("User is not the owner of this order. orderId={}, clientId={}", orderId, order.getClient().getId());
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

    public boolean haveSpecialistProposal(Order order, OrderProposal proposal) {
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
