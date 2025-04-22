package fit.biejk.resource;

import fit.biejk.dto.SpecialistDto;
import fit.biejk.entity.Review;
import fit.biejk.entity.ServiceOffering;
import fit.biejk.entity.Specialist;
import fit.biejk.mapper.ReviewMapper;
import fit.biejk.mapper.SpecialistMapper;
import fit.biejk.service.AuthService;
import fit.biejk.service.ReviewService;
import fit.biejk.service.SpecialistService;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * REST resource for managing specialists.
 * <p>
 * Provides endpoints for retrieving, updating, and deleting specialist data,
 * as well as accessing and modifying the current specialist's profile.
 * </p>
 */
@Path("/specialist")
@Slf4j
public class SpecialistResource {

    /** Service for handling business logic related to specialists. */
    @Inject
    private SpecialistService specialistService;

    /** Mapper for converting between Specialist entities and DTOs. */
    @Inject
    private SpecialistMapper specialistMapper;

    /** Service for retrieving authenticated user context and identity. */
    @Inject
    private AuthService authService;

    /** Mapper for converting between Review and ReviewDto. */
    @Inject
    private ReviewMapper reviewMapper;

    /** Service for accessing client reviews. */
    @Inject
    private ReviewService reviewService;

    /**
     * Retrieves a list of all specialists.
     *
     * @return HTTP response containing a list of all specialists
     */
    @GET
    @PermitAll
    public Response getAll() {
        log.info("Get all specialists");
        List<Specialist> result = specialistService.getAll();
        log.debug("Found {} specialists", result.size());
        return Response.ok(specialistMapper.toDtoList(result)).build();
    }

    /**
     * Retrieves a specialist by their ID.
     *
     * @param id ID of the specialist
     * @return HTTP response containing the specialist
     */
    @GET
    @Path("/{id}")
    @PermitAll
    public Response getById(@PathParam("id") final Long id) {
        log.info("Get specialist by ID={}", id);
        Specialist result = specialistService.getById(id);
        return Response.ok(specialistMapper.toDto(result)).build();
    }

    /**
     * Creates a new specialist.
     * Currently disabled with {@code @DenyAll}.
     *
     * @param dto data for the new specialist
     * @return HTTP response with the created specialist or error
     */
    @POST
    @DenyAll
    public Response create(@Valid final SpecialistDto dto) {
        log.info("Create specialist request: {}", dto);
        Specialist entity = specialistMapper.toEntity(dto);
        try {
            Specialist result = specialistService.create(entity);
            log.debug("Specialist created with ID={}", result.getId());
            return Response.ok(specialistMapper.toDto(result)).build();
        } catch (IllegalArgumentException e) {
            log.error("Error creating specialist: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    /**
     * Updates a specialist by ID.
     * Currently disabled with {@code @DenyAll}.
     *
     * @param id  ID of the specialist to update
     * @param dto updated data
     * @return HTTP response with updated specialist
     */
    @PUT
    @Path("/{id}")
    @DenyAll
    public Response update(@PathParam("id") final Long id, @Valid final SpecialistDto dto) {
        log.info("Update specialist request: ID={}, dto={}", id, dto);
        Specialist entity = specialistMapper.toEntity(dto);
        Specialist result = specialistService.update(id, entity);
        log.debug("Specialist updated with ID={}", result.getId());
        return Response.ok(specialistMapper.toDto(result)).build();
    }

    /**
     * Deletes a specialist by ID.
     * Currently disabled with {@code @DenyAll}.
     *
     * @param id ID of the specialist to delete
     * @return HTTP response
     */
    @DELETE
    @Path("/{id}")
    @DenyAll
    public Response delete(@PathParam("id") final Long id) {
        log.info("Delete specialist request: ID={}", id);
        specialistService.delete(id);
        return Response.ok().build();
    }

    /**
     * Retrieves the profile of the currently authenticated specialist.
     *
     * @return HTTP response containing the specialist's profile
     */
    @GET
    @Path("/me")
    @RolesAllowed("SPECIALIST")
    public Response getProfile() {
        Long specialistId = authService.getCurrentUserId();
        log.info("Get profile for specialist ID={}", specialistId);
        Specialist specialist = specialistService.getById(specialistId);
        return Response.ok(specialistMapper.toDto(specialist)).build();
    }

    /**
     * Updates the profile of the currently authenticated specialist.
     *
     * @param dto updated profile data
     * @return HTTP response with updated profile
     */
    @PUT
    @Path("/me")
    @RolesAllowed("SPECIALIST")
    public Response updateProfile(@Valid final SpecialistDto dto) {
        Long id = authService.getCurrentUserId();
        log.info("Update profile for specialist ID={} with dto={}", id, dto);
        Specialist specialist = specialistService.update(id, specialistMapper.toEntity(dto));
        log.debug("Specialist profile updated, ID={}", specialist.getId());
        return Response.ok(specialistMapper.toDto(specialist)).build();
    }

    /**
     * Deletes the profile of the currently authenticated specialist.
     *
     * @return HTTP response
     */
    @DELETE
    @Path("/me")
    @RolesAllowed("SPECIALIST")
    public Response deleteProfile() {
        Long id = authService.getCurrentUserId();
        log.info("Delete profile for specialist ID={}", id);
        specialistService.delete(id);
        return Response.ok().build();
    }

    /**
     * Retrieves all reviews submitted by the currently authenticated client.
     *
     * @return HTTP response containing list of reviews
     */
    @GET
    @Path("/me/review")
    @PermitAll
    public Response getReviews() {
        Long clientId = authService.getCurrentUserId();
        List<Review> res = reviewService.getByClientId(clientId);
        return Response.ok(reviewMapper.toDtoList(res)).build();
    }

    /**
     * Updates the list of service offerings for the currently authenticated specialist.
     *
     * @param serviceOfferings list of updated service offerings
     * @return HTTP response with updated specialist data
     */
    @PUT
    @Path("/me/service")
    @RolesAllowed("SPECIALIST")
    public Response updateServiceOffering(final List<ServiceOffering> serviceOfferings) {
        Long id = authService.getCurrentUserId();
        log.info("Add serviceOffering for specialist ID={}", id);
        Specialist specialist = specialistService.updateServiceOfferings(id, serviceOfferings);
        return Response.ok(specialistMapper.toDto(specialist)).build();
    }
}
