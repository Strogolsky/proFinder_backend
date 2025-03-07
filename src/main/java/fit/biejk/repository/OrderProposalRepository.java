package fit.biejk.repository;

import fit.biejk.entity.Order;
import fit.biejk.entity.OrderProposal;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class OrderProposalRepository implements PanacheRepository<OrderProposal> {
    public List<OrderProposal> findByOrder(Order order) {
        return find("order", order).list();
    }
}
