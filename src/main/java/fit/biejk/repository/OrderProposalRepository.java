package fit.biejk.repository;

import fit.biejk.entity.OrderProposal;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderProposalRepository implements PanacheRepository<OrderProposal> {
}
