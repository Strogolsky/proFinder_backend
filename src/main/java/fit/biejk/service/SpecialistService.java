package fit.biejk.service;

import fit.biejk.entity.Specialist;
import fit.biejk.repository.SpecialistRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@ApplicationScoped
public class SpecialistService {

    @Inject
    SpecialistRepository specialistRepository;

    @Inject
    UserService userService;

    @Transactional
    public Specialist create(Specialist specialist) {
        log.info("Create specialist: {}", specialist.getEmail());
        userService.checkUniqueEmail(specialist.getEmail());
        specialistRepository.persist(specialist);
        log.debug("Specialist created with ID={}", specialist.getId());
        return specialist;
    }

    public List<Specialist> getAll() {
        log.info("Get all specialists");
        List<Specialist> specialists = specialistRepository.listAll();
        log.debug("Found {} specialists", specialists.size());
        return specialists;
    }

    public Specialist getById(Long id) {
        log.info("Get specialist by ID={}", id);
        Specialist result = specialistRepository.findById(id);
        if (result == null) {
            log.error("Specialist with ID={} not found", id);
            throw new NotFoundException("Specialist with id " + id + " not found");
        }
        return result;
    }

    @Transactional
    public Specialist update(Long id, Specialist specialist) {
        log.info("Update specialist: ID={}, email={}", id, specialist.getEmail());
        Specialist old = getById(id);
        userService.update(id, specialist);
        old.setDescription(specialist.getDescription());
        old.setSpecialization(specialist.getSpecialization());
        specialistRepository.flush();
        old.getSchedule().size(); // todo fix when add DTO
        log.debug("Specialist updated with ID={}", id);
        return old;
    }

    @Transactional
    public void delete(Long id) {
        log.info("Delete specialist: ID={}", id);
        userService.delete(id);
        log.debug("Specialist deleted with ID={}", id);
    }
}
