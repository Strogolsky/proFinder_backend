package fit.biejk.resource;

import fit.biejk.dto.ClientDto;
import fit.biejk.entity.Client;
import fit.biejk.entity.Review;
import fit.biejk.mapper.ClientMapper;
import fit.biejk.mapper.ReviewMapper;
import fit.biejk.service.AuthService;
import fit.biejk.service.ClientService;
import fit.biejk.service.ReviewService;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * REST resource for managing clients.
 * <p>
 * Provides endpoints for retrieving and managing client data.
 * </p>
 */
@Path("/client")
@Slf4j
public class ClientResource {
    /**
     * Mapper for converting between Client entities and DTOs.
     */
    @Inject
    private ClientMapper clientMapper;

    /**
     * Service layer for handling client-related business logic.
     */
    @Inject
    private ClientService clientService;

    /**
     * Service for authentication and user identity operations.
     */
    @Inject
    private AuthService authService;

    @Inject
    private ReviewMapper reviewMapper;

    @Inject
    private ReviewService reviewService;

    /**
     * Retrieves all clients.
     *
     * @return list of all clients
     */
    @GET
    @PermitAll
    public Response getAll() {
        log.info("getAll request");
        List<Client> result = clientService.getAll();
        log.debug("Found {} clients", result.size());
        return Response.ok(clientMapper.toDtoList(result)).build();
    }

    /**
     * Retrieves a client by ID.
     *
     * @param id the client ID
     * @return client data
     */
    @GET
    @Path("/{id}")
    @PermitAll
    public Response getById(@PathParam("id") final Long id) {
        log.info("getById request: {}", id);
        Client result = clientService.getById(id);
        log.debug("Found client with ID={}", result.getId());
        return Response.ok(clientMapper.toDto(result)).build();
    }

    /**
     * Creates a new client.
     * Currently disabled by @DenyAll.
     *
     * @param dto client data
     * @return created client or error
     */
    @POST
    @DenyAll
    public Response create(@Valid final ClientDto dto) {
        log.info("create request: {}", dto);
        Client entity = clientMapper.toEntity(dto);
        try {
            Client result = clientService.create(entity);
            log.debug("Created client with ID={}", result.getId());
            return Response.ok(clientMapper.toDto(result)).build();
        } catch (IllegalArgumentException e) {
            log.error("Create failed: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    /**
     * Updates a client by ID.
     * Currently disabled by @DenyAll.
     *
     * @param id  client ID
     * @param dto updated client data
     * @return updated client
     */
    @PUT
    @Path("/{id}")
    @DenyAll
    public Response update(@PathParam("id") final Long id, @Valid final ClientDto dto) {
        log.info("update request: id={}, dto={}", id, dto);
        Client entity = clientMapper.toEntity(dto);
        Client result = clientService.update(id, entity);
        log.debug("Updated client with ID={}", result.getId());
        return Response.ok(clientMapper.toDto(result)).build();
    }

    /**
     * Deletes a client by ID.
     * Currently disabled by @DenyAll.
     *
     * @param id client ID
     * @return response status
     */
    @DELETE
    @Path("/{id}")
    @DenyAll
    public Response delete(@PathParam("id") final Long id) {
        log.info("delete request: {}", id);
        clientService.delete(id);
        log.debug("Deleted client with ID={}", id);
        return Response.ok().build();
    }

    /**
     * Retrieves the profile of the currently authenticated client.
     *
     * @return client profile
     */
    @GET
    @Path("/me")
    @RolesAllowed("CLIENT")
    public Response getProfile() {
        log.info("getProfile request");
        Long clientId = authService.getCurrentUserId();
        Client client = clientService.getById(clientId);
        log.debug("Profile client ID={}", clientId);
        return Response.ok(clientMapper.toDto(client)).build();
    }

    /**
     * Updates the profile of the currently authenticated client.
     *
     * @param dto updated client data
     * @return updated client profile
     */
    @PUT
    @Path("/me")
    @RolesAllowed("CLIENT")
    public Response updateProfile(@Valid final ClientDto dto) {
        log.info("updateProfile request: {}", dto);
        Long id = authService.getCurrentUserId();
        Client client = clientService.update(id, clientMapper.toEntity(dto));
        log.debug("Updated profile for client with ID={}", client.getId());
        return Response.ok(clientMapper.toDto(client)).build();
    }

    /**
     * Deletes the profile of the currently authenticated client.
     *
     * @return response status
     */
    @DELETE
    @Path("/me")
    @RolesAllowed("CLIENT")
    public Response deleteProfile() {
        log.info("deleteProfile request");
        Long id = authService.getCurrentUserId();
        clientService.delete(id);
        log.debug("Deleted profile for client with ID={}", id);
        return Response.ok().build();
    }

    @GET
    @Path("/me/review")
    @RolesAllowed("CLIENT")
    public Response getReviews() {
        Long clientId = authService.getCurrentUserId();
        List<Review> res = reviewService.getByClientId(clientId);
        return Response.ok(reviewMapper.toDtoList(res)).build();
    }

}
