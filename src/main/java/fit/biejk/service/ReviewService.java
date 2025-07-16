package fit.biejk.service;

import fit.biejk.entity.Client;
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
 * Provides functionality for creating reviews and retrieving them by client, specialist, or order.
 * Also updates the average rating of specialists upon new review creation.
 * </p>
 */
@Slf4j
@ApplicationScoped
public class ReviewService {

    /** Repository for accessing persisted reviews. */
    @Inject
    private ReviewRepository reviewRepository;

    /** Service for retrieving and updating specialists. */
    @Inject
    private SpecialistService specialistService;

    /** Service for retrieving clients. */
    @Inject
    private ClientService clientService;

    /**
     * Persists a new review and updates the specialist's average rating.
     *
     * @param review the review to be saved
     * @return the persisted {@link Review}
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

    /**
     * Retrieves all reviews written for a specific specialist.
     *
     * @param specialistId the specialist's ID
     * @return list of reviews for the specialist
     */
    public List<Review> getBySpecialistId(final Long specialistId) {
        log.info("Fetching reviews for specialistId={}", specialistId);
        Specialist specialist = specialistService.getById(specialistId);
        List<Review> reviews = reviewRepository.findBySpecialist(specialist);
        log.debug("Found {} reviews for specialistId={}", reviews.size(), specialistId);
        return reviews;
    }

    /**
     * Retrieves all reviews written by a specific client.
     *
     * @param clientId the client's ID
     * @return list of reviews created by the client
     */
    public List<Review> getByClientId(final Long clientId) {
        log.info("Fetching reviews by clientId={}", clientId);
        Client client = clientService.getById(clientId);
        List<Review> reviews = reviewRepository.findByClient(client);
        log.debug("Found {} reviews by clientId={}", reviews.size(), clientId);
        return reviews;
    }

}
