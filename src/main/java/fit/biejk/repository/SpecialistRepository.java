package fit.biejk.repository;

import fit.biejk.entity.Specialist;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SpecialistRepository implements PanacheRepository<Specialist> {
}
