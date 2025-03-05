package fit.biejk.service;

import fit.biejk.entity.Order;
import fit.biejk.entity.OrderProposal;
import fit.biejk.entity.ProposalStatus;
import fit.biejk.repository.OrderProposalRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class OrderProposalService {
    @Inject
    OrderProposalRepository orderProposalRepository;

    public OrderProposal create(OrderProposal orderProposal) {
        orderProposal.setStatus(ProposalStatus.CREATED);
        orderProposalRepository.persist(orderProposal);
        return orderProposal;
    }
    public void approveProposal(Order order, Long proposalId) {
        OrderProposal approvedProposal = orderProposalRepository.findById(proposalId);

        approvedProposal.setStatus(ProposalStatus.APPROVED);
        orderProposalRepository.persist(approvedProposal);

        List<OrderProposal> allProposals = orderProposalRepository.findByOrder(order);
        for (OrderProposal proposal : allProposals) {
            if (!proposal.getId().equals(approvedProposal.getId())) {
                proposal.setStatus(ProposalStatus.REJECTED);
                orderProposalRepository.persist(proposal);
            }
        }
    }

}
