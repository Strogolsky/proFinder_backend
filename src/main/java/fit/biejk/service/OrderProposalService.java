package fit.biejk.service;

import fit.biejk.entity.OrderProposal;
import fit.biejk.repository.OrderProposalRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class OrderProposalService {
    @Inject
    OrderProposalRepository orderProposalRepository;

    public OrderProposal create(OrderProposal orderProposal) {
        orderProposalRepository.persist(orderProposal);
        return orderProposal;
    }
}
