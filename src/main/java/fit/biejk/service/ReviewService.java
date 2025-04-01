package fit.biejk.service;

import fit.biejk.entity.Client;
import fit.biejk.entity.Order;
import fit.biejk.entity.Review;
import fit.biejk.entity.Specialist;
import fit.biejk.repository.ReviewRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Service for managing {@link Review} entities.
 * <p>
 * Handles creation of reviews and triggers rating recalculation for specialists.
 * </p>
 */
@Slf4j
@ApplicationScoped
public class ReviewService {

    @Inject
    ReviewRepository reviewRepository;

    @Inject
    SpecialistService specialistService;

    @Inject
    OrderService orderService;

    @Inject
    ClientService clientService;

    /**
     * Creates a new review and updates the average rating for the associated specialist.
     *
     * @param review the review to be persisted
     * @return the persisted review
     */
    @Transactional
    public Review create(final Review review) {
        log.info("Creating review: specialistId={}, clientId={}, rating={}",
                review.getSpecialist().getId(),
                review.getClient().getId(),
                review.getRating());

        reviewRepository.persist(review);
        log.debug("Review persisted: reviewId={}", review.getId());


        specialistService.computeAverageRating(review.getSpecialist().getId());
        log.debug("Average rating recalculated for specialistId={}", review.getSpecialist().getId());

        return review;
    }

    public List<Review> getBySpecialistId(final Long specialistId) {
        Specialist specialist = specialistService.getById(specialistId);
        return reviewRepository.findBySpecialist(specialist);
    }

    public List<Review> getByClientId(final Long clientId) {
        Client client = clientService.getById(clientId);
        return reviewRepository.findByClient(client);
    }

    public Review getByOrderId(final Long orderId) {
        Order order = orderService.getById(orderId);
        return reviewRepository.findByOrder(order);
    }
}
