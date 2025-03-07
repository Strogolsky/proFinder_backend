package fit.biejk.service;

import fit.biejk.entity.Specialist;
import fit.biejk.repository.SpecialistRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;

@ApplicationScoped
public class SpecialistService {
    @Inject
    SpecialistRepository specialistRepository;
    @Inject
    UserService userService;

    @Transactional
    public Specialist create(Specialist specialist)  {
        userService.checkUniqueEmail(specialist.getEmail());
        specialistRepository.persist(specialist);
        return specialist;
    }

    public List<Specialist> getAll() {
        return specialistRepository.listAll();
    }

    public Specialist getById(Long id) {
        Specialist result = specialistRepository.findById(id);
        if (result == null) {
            throw new NotFoundException("Specialist with id " + id + " not found");
        }
        return result;
    }

    @Transactional
    public Specialist update(Long id, Specialist specialist) {
        Specialist old = getById(id);
        userService.update(id, specialist);
        old.setDescription(specialist.getDescription());
        old.setSpecialization(specialist.getSpecialization());
        specialistRepository.flush();
        old.getSchedule().size(); // todo fix when add DTO

        return old;
    }

    @Transactional
    public void delete(Long id) {
        userService.delete(id);
    }
}
