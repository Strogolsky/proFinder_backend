package fit.biejk.repository;

import fit.biejk.entity.Location;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LocationRepository implements PanacheRepository<Location> {

    public Location findByName(final String name) {
        return find("name", name).firstResult();
    }

}
