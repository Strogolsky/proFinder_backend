package fit.biejk.service;

import fit.biejk.entity.Order;
import fit.biejk.entity.OrderProposal;
import fit.biejk.entity.ProposalStatus;
import fit.biejk.repository.OrderProposalRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@ApplicationScoped
public class OrderProposalService {

    @Inject
    OrderProposalRepository orderProposalRepository;

    public OrderProposal create(OrderProposal orderProposal) {
        log.info("Create orderProposal for orderId={}, specialistId={}",
                orderProposal.getOrder().getId(),
                orderProposal.getSpecialist().getId());
        orderProposal.setStatus(ProposalStatus.CREATED);
        orderProposalRepository.persist(orderProposal);
        log.debug("OrderProposal created with ID={}", orderProposal.getId());
        return orderProposal;
    }

    public void approveProposal(Order order, Long proposalId) {
        log.info("Approve proposal: orderId={}, proposalId={}", order.getId(), proposalId);
        OrderProposal approvedProposal = orderProposalRepository.findById(proposalId);
        approvedProposal.setStatus(ProposalStatus.APPROVED);
        orderProposalRepository.persist(approvedProposal);
        log.debug("Approved proposal with ID={}", approvedProposal.getId());
        List<OrderProposal> allProposals = getByOrderId(order);
        for (OrderProposal proposal : allProposals) {
            if (!proposal.getId().equals(approvedProposal.getId())) {
                proposal.setStatus(ProposalStatus.REJECTED);
                orderProposalRepository.persist(proposal);
                log.debug("Rejected proposal with ID={}", proposal.getId());
            }
        }
    }

    public OrderProposal getById(Long proposalId) {
        log.info("Get proposal by ID={}", proposalId);
        OrderProposal result = orderProposalRepository.findById(proposalId);
        if (result == null) {
            log.error("OrderProposal with ID={} not found", proposalId);
            throw new NotFoundException("OrderProposal with id " + proposalId + " not found");
        }
        log.debug("Found proposal with ID={}", result.getId());
        return result;
    }

    public List<OrderProposal> getByOrderId(Order order) {
        log.info("Get proposals for order ID={}", order.getId());
        List<OrderProposal> proposals = orderProposalRepository.findByOrder(order);
        log.debug("Found {} proposals for order ID={}", proposals.size(), order.getId());
        return proposals;
    }
}
