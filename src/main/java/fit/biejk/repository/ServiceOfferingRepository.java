package fit.biejk.repository;

import fit.biejk.entity.ServiceOffering;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ServiceOfferingRepository implements PanacheRepository<ServiceOffering> {
}
