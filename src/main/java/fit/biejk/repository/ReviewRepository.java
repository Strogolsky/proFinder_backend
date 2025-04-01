package fit.biejk.repository;

import fit.biejk.entity.Client;
import fit.biejk.entity.Order;
import fit.biejk.entity.Review;
import fit.biejk.entity.Specialist;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ReviewRepository implements PanacheRepository<Review> {
    public List<Review> findBySpecialist(final Specialist specialist) {
        return find("specialist", specialist).list();
    }

    public Review findByOrder(final Order order) {
        return find("order", order).firstResult();
    }

    public List<Review> findByClient(final Client client) {
        return find("client", client).list();
    }
}
