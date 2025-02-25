package fit.biejk.service;

import fit.biejk.entity.Specialist;
import fit.biejk.repository.SpecialistRepository;
import fit.biejk.repository.UserRepository;
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
        return specialistRepository.findById(id);
    }

    @Transactional
    public Specialist update(Long id, Specialist specialist) {
        Specialist old = specialistRepository.findById(id);
        if(old == null) {
            throw new NotFoundException("Specialist with id " + id + " not found");
        }
        userService.updateCommonFields(old, specialist);
        old.setDescription(specialist.getDescription());
        specialistRepository.flush();
        old.getSchedule().size(); // todo fix when add DTO

        return old;
    }

    @Transactional
    public void delete(Long id) {
        Specialist specialist = specialistRepository.findById(id);
        specialistRepository.delete(specialist);
    }
}
