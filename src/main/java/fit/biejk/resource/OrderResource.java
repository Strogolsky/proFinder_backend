package fit.biejk.resource;

import fit.biejk.dto.OrderDto;
import fit.biejk.dto.OrderProposalDto;
import fit.biejk.entity.*;
import fit.biejk.mapper.OrderMapper;
import fit.biejk.mapper.OrderProposalMapper;
import fit.biejk.service.*;
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
 * REST resource for managing orders and proposals.
 * <p>
 * Supports creating, updating, confirming and retrieving orders, as well as submitting and managing proposals.
 * </p>
 */
@Path("/order")
@Slf4j
public class OrderResource {

    /**
     * Service handling order-related business logic.
     */
    @Inject
    private  OrderService orderService;

    /**
     * Mapper for converting between Order entities and DTOs.
     */
    @Inject
    private OrderMapper orderMapper;

    /**
     * Service for accessing client information.
     */
    @Inject
    private ClientService clientService;

    /**
     * Service for accessing specialist information.
     */
    @Inject
    private SpecialistService specialistService;

    /**
     * Service for authentication and identity resolution.
     */
    @Inject
    private AuthService authService;

    /**
     * Mapper for converting between OrderProposal entities and DTOs.
     */
    @Inject
    private OrderProposalMapper orderProposalMapper;


    /**
     * Creates a new order for the authenticated client.
     *
     * @param dto order data
     * @return created order
     */
    @POST
    @RolesAllowed("CLIENT")
    public Response create(@Valid final OrderDto dto) {
        log.info("Create order request: {}", dto);
        Order order = orderMapper.toEntity(dto);
        Long clientId = authService.getCurrentUserId();
        Client client = clientService.getById(clientId);
        order.setClient(client);
        order.setLocation(client.getLocation());
        Order result = orderService.create(order);
        log.debug("Order created with ID={}", result.getId());
        return Response.ok(orderMapper.toDto(result)).build();
    }

    /**
     * Updates an existing order by ID.
     *
     * @param orderId order ID
     * @param dto     updated order data
     * @return updated order
     */
    @PUT
    @Path("/{orderId}")
    @RolesAllowed("CLIENT")
    public Response update(@PathParam("orderId") final Long orderId, @Valid final OrderDto dto) {
        log.info("Update order request: orderId={}, dto={}", orderId, dto);
        Order order = orderMapper.toEntity(dto);
        Order result = orderService.update(orderId, order);
        log.debug("Order updated with ID={}", result.getId());
        return Response.ok(orderMapper.toDto(result)).build();
    }

    /**
     * Cancels an order by ID.
     *
     * @param orderId order ID
     * @return canceled order
     */
    @PUT
    @Path("/{orderId}/cancel")
    @RolesAllowed("CLIENT")
    public Response cancel(@PathParam("orderId") final Long orderId) {
        log.info("Cancel order request: orderId={}", orderId);
        Order result = orderService.cancel(orderId);
        log.debug("Order canceled with ID={}", result.getId());
        return Response.ok(orderMapper.toDto(result)).build();
    }

    /**
     * Submits a proposal for an order by the authenticated specialist.
     *
     * @param orderId      ID of the order to propose on
     * @param proposalDto  proposal data
     * @return created proposal
     */
    @PUT
    @Path("/{orderId}/proposal")
    @RolesAllowed("SPECIALIST")
    public Response proposal(@PathParam("orderId") final Long orderId, @Valid final OrderProposalDto proposalDto) {
        log.info("Proposal request: orderId={}, proposalDto={}", orderId, proposalDto);
        OrderProposal proposal = orderProposalMapper.toEntity(proposalDto);
        Long specialistId = authService.getCurrentUserId();
        Specialist specialist = specialistService.getById(specialistId);
        proposal.setSpecialist(specialist);
        OrderProposal result = orderService.proposal(orderId, proposal);
        log.debug("Proposal created with ID={}", result.getId());
        return Response.ok(orderProposalMapper.toDto(result)).build();
    }

    /**
     * Retrieves an order by ID.
     *
     * @param orderId order ID
     * @return order data
     */
    @GET
    @Path("/{orderId}")
    @PermitAll
    public Response getById(@PathParam("orderId") final Long orderId) {
        log.info("Get order by ID: {}", orderId);
        Order result = orderService.getById(orderId);
        return Response.ok(orderMapper.toDto(result)).build();
    }

    /**
     * Retrieves a list of all orders.
     *
     * @return list of all orders
     */
    @GET
    @PermitAll
    public Response getAll() {
        log.info("Get all orders");
        List<Order> result = orderService.getAll();
        log.debug("Orders found: {}", result.size());
        return Response.ok(orderMapper.toDtoList(result)).build();
    }

    /**
     * Deletes an order by ID.
     *
     * @param orderId order ID
     * @return empty response
     */
    @DELETE
    @Path("/{orderId}")
    @RolesAllowed("CLIENT")
    public Response delete(@PathParam("orderId") final Long orderId) {
        log.info("Delete order request: orderId={}", orderId);
        orderService.delete(orderId);
        return Response.ok().build();
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
    @Path("/client")
    @RolesAllowed("CLIENT")
    public Response getByClient() {
        Long clientId = authService.getCurrentUserId();
        log.info("Get client request: clientId={}", clientId);
        List<Order> result = orderService.getByClientId(clientId);
        return Response.ok(orderMapper.toDtoList(result)).build();
    }

    /**
     * Retrieves all active orders assigned to a specific specialist.
     * <p>
     * Only accessible to authenticated users with the SPECIALIST role.
     * </p>
     *
     * @return list of orders currently assigned to the specialist
     */
    @GET
    @Path("/specialist")
    @RolesAllowed("SPECIALIST")
    public Response getBySpecialist() {
        Long specialistId = authService.getCurrentUserId();
        log.info("Get assigned by specialist id: specialistId={}", specialistId);
        List<Order> result = orderService.getBySpecialistId(specialistId);
        return Response.ok(orderMapper.toDtoList(result)).build();
    }




}
