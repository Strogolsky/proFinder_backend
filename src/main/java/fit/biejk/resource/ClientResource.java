package fit.biejk.resource;

import fit.biejk.dto.ClientDto;
import fit.biejk.dto.PageRequest;
import fit.biejk.entity.Client;
import fit.biejk.entity.Order;
import fit.biejk.entity.Review;
import fit.biejk.mapper.ClientMapper;
import fit.biejk.mapper.OrderMapper;
import fit.biejk.mapper.ReviewMapper;
import fit.biejk.service.AuthService;
import fit.biejk.service.ClientService;
import fit.biejk.service.OrderService;
import fit.biejk.service.ReviewService;
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
 * REST resource for managing clients.
 * <p>
 * Provides endpoints for retrieving and managing client data.
 * </p>
 */
@Path("/v1/clients")
@Slf4j
public class ClientResource {
    /**
     * Mapper for converting between Client entities and DTOs.
     */
    @Inject
    private ClientMapper clientMapper;

    @Inject
    private OrderMapper orderMapper;

    @Inject
    private OrderService orderService;

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

    /** Maps Review entity to ReviewDto and vice versa. */
    @Inject
    private ReviewMapper reviewMapper;

    /** Service for accessing client reviews. */
    @Inject
    private ReviewService reviewService;

    /**
     * Retrieves all clients.
     *
     * @return list of all clients
     */
    @GET
    @PermitAll
    public Response getClients(@BeanParam PageRequest pagination) {
        log.info("getAll request");
        List<Client> result = clientService.getAll(
                pagination.getPage(),
                pagination.getSize()
        );
        log.debug("Found {} clients", result.size());
        return Response.ok(clientMapper.toDtoList(result)).build();
    }

    /**
     * Retrieves a client by ID.
     *
     * @param clientId the client ID
     * @return client data
     */
    @GET
    @Path("/{clientId}")
    @PermitAll
    public Response getById(@PathParam("clientId") final Long clientId) {
        log.info("getById request: {}", clientId);
        Client result = clientService.getById(clientId);
        log.debug("Found client with ID={}", result.getId());
        return Response.ok(clientMapper.toDto(result)).build();
    }

    /**
     * Updates a client by ID.
     * Currently disabled by @DenyAll.
     *
     * @param clientId  client ID
     * @param dto updated client data
     * @return updated client
     */
    @PUT
    @Path("/{clientId}")
    @DenyAll
    public Response update(@PathParam("clientId") final Long clientId, @Valid final ClientDto dto) {
        log.info("update request: clientId={}, dto={}", clientId, dto);
        Client entity = clientMapper.toEntity(dto);
        Client result = clientService.update(clientId, entity);
        log.debug("Updated client with ID={}", result.getId());
        return Response.ok(clientMapper.toDto(result)).build();
    }

    /**
     * Deletes a client by ID.
     * Currently disabled by @DenyAll.
     *
     * @param clientId client ID
     * @return response status
     */
    @DELETE
    @Path("/{clientId}")
    @DenyAll
    public Response delete(@PathParam("clientId") final Long clientId) {
        log.info("delete request: {}", clientId);
        clientService.delete(clientId);
        log.debug("Deleted client with ID={}", clientId);
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

    /**
     * Retrieves all reviews submitted by the currently authenticated client.
     *
     * @return list of client's reviews
     */
    @GET
    @Path("/me/reviews")
    @RolesAllowed("CLIENT")
    public Response getReviews(
            @BeanParam PageRequest pagination

    ) {
        Long clientId = authService.getCurrentUserId();
        List<Review> res = reviewService.getByClientId(
                clientId,
                pagination.getPage(),
                pagination.getSize());
        return Response.ok(reviewMapper.toDtoList(res)).build();
    }

    /**
     * Retrieves all orders created by a specific client.
     * <p>
     * Only accessible to authenticated users with the CLIENT role.
     * </p>
     *
     * @return list of orders created by the client
     */
    @GET
    @Path("/me/orders")
    @RolesAllowed("CLIENT")
    public Response getOrders(
            @BeanParam PageRequest pagination
    ) {
        Long clientId = authService.getCurrentUserId();
        log.info("Get client request: clientId={}", clientId);
        List<Order> result = orderService.getByClientId(
                clientId,
                pagination.getPage(),
                pagination.getSize());
        return Response.ok(orderMapper.toDtoList(result)).build();
    }


}
