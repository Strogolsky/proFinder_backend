package fit.biejk.service;

import fit.biejk.entity.Review;
import fit.biejk.entity.ServiceOffering;
import fit.biejk.entity.Specialist;
import fit.biejk.repository.SpecialistRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Service class for managing {@link Specialist} entities.
 * <p>
 * Handles creation, retrieval, updating, and deletion of specialists.
 * Delegates common user-related logic to {@link UserService}.
 * </p>
 */
@Slf4j
@ApplicationScoped
public class SpecialistService {

    /**
     * Repository for accessing specialist data.
     */
    @Inject
    private SpecialistRepository specialistRepository;

    /**
     * Service for handling common user operations (validation, update, delete).
     */
    @Inject
    private UserService userService;

    /**
     * Creates and persists a new specialist.
     * Performs email uniqueness check before creation.
     *
     * @param specialist specialist to be created
     * @return created specialist
     */
    @Transactional
    public Specialist create(final Specialist specialist) {
        log.info("Create specialist: {}", specialist.getEmail());
        userService.checkUniqueEmail(specialist.getEmail());
        specialistRepository.persist(specialist);
        log.debug("Specialist created with ID={}", specialist.getId());
        return specialist;
    }

    /**
     * Retrieves all specialists from the database.
     *
     * @return list of all specialists
     */
    public List<Specialist> getAll() {
        log.info("Get all specialists");
        List<Specialist> specialists = specialistRepository.listAll();
        log.debug("Found {} specialists", specialists.size());
        return specialists;
    }

    /**
     * Retrieves a specialist by their ID.
     *
     * @param id specialist ID
     * @return found specialist
     * @throws NotFoundException if specialist is not found
     */
    public Specialist getById(final Long id) {
        log.info("Get specialist by ID={}", id);
        Specialist result = specialistRepository.findById(id);
        if (result == null) {
            log.error("Specialist with ID={} not found", id);
            throw new NotFoundException("Specialist with id " + id + " not found");
        }
        return result;
    }

    /**
     * Updates an existing specialist.
     * Delegates base update logic to {@link UserService}, then applies additional updates.
     *
     * @param id         specialist ID
     * @param specialist updated specialist data
     * @return updated specialist
     */
    @Transactional
    public Specialist update(final Long id, final Specialist specialist) {
        log.info("Update specialist: ID={}, email={}", id, specialist.getEmail());
        Specialist old = getById(id);
        userService.update(id, specialist);
        old.setDescription(specialist.getDescription());
        specialistRepository.flush();
        log.debug("Specialist updated with ID={}", id);
        return old;
    }

    /**
     * Deletes a specialist by ID.
     * Delegates deletion logic to {@link UserService}.
     *
     * @param id specialist ID
     */
    @Transactional
    public void delete(final Long id) {
        log.info("Delete specialist: ID={}", id);
        userService.delete(id);
        log.debug("Specialist deleted with ID={}", id);
    }

    /**
     * Recalculates and updates the average rating for a specialist.
     * <p>
     * Computes the arithmetic mean from all reviews linked to the specialist.
     * If no reviews are found, the rating is set to {@code null}.
     * </p>
     *
     * @param specialistId the ID of the specialist whose rating should be updated
     */
    @Transactional
    public void computeAverageRating(final Long specialistId) {
        Specialist specialist = getById(specialistId);
        List<Review> reviews = specialist.getReviews();

        if (reviews == null || reviews.isEmpty()) {
            log.warn("No reviews found for specialistId={}. Setting averageRating to null.", specialistId);
            specialist.setAverageRating(null);
        } else {
            double sum = 0;
            for (Review review : reviews) {
                sum += review.getRating();
            }
            double average = sum / reviews.size();
            specialist.setAverageRating(average);
            log.debug("Computed new averageRating={} for specialistId={}", average, specialistId);
        }

        specialistRepository.persist(specialist);
        log.info("Updated averageRating for specialistId={}", specialistId);
    }

    /**
     * Updates the list of service offerings associated with the specialist.
     *
     * @param specialistId ID of the specialist to update
     * @param serviceOfferings new list of service offerings to associate
     * @return updated specialist
     */
    @Transactional
    public Specialist updateServiceOfferings(final Long specialistId,
                                             final List<ServiceOffering> serviceOfferings) {
        log.info("Update serviceOfferings: specialistId={}", specialistId);
        Specialist specialist = getById(specialistId);
        specialist.setServiceOfferings(serviceOfferings);

        specialistRepository.persist(specialist);
        log.debug("Updated serviceOfferings with ID={}", specialistId);
        return specialist;
    }



}
