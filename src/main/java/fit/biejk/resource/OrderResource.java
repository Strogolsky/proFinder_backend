package fit.biejk.resource;

import fit.biejk.dto.*;
import fit.biejk.entity.*;
import fit.biejk.mapper.OrderMapper;
import fit.biejk.mapper.OrderProposalMapper;
import fit.biejk.search.OrderSearchMapper;
import fit.biejk.search.OrderSearchService;
import fit.biejk.service.*;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

/**
 * REST resource for managing orders and proposals.
 * <p>
 * Supports creating, updating, confirming and retrieving orders, as well as submitting and managing proposals.
 * </p>
 */
@Path("/v1/orders")
@Slf4j
public class OrderResource {

    /** Service for managing order proposals. */
    @Inject
    private OrderProposalService orderProposalService;
    /**
     * Service handling order-related business logic.
     */
    @Inject
    private  OrderService orderService;

    /**
     * Service for searching and filtering orders.
     */
    @Inject
    private OrderSearchService orderSearchService;

    /**
     * Mapper for converting order search results and criteria.
     */
    @Inject
    private OrderSearchMapper orderSearchMapper;

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

        return Response.status(Response.Status.CREATED).entity(orderMapper.toDto(result)).build();
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
    @POST
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
    @POST
    @Path("/{orderId}/proposals")
    @RolesAllowed("SPECIALIST")
    public Response proposal(
            @PathParam("orderId") final Long orderId,
            @Valid final OrderProposalDto proposalDto) {
        log.info("Proposal request: orderId={}, proposalDto={}", orderId, proposalDto);
        OrderProposal proposal = orderProposalMapper.toEntity(proposalDto);
        Long specialistId = authService.getCurrentUserId();

        OrderProposal result = orderService.proposal(orderId, specialistId, proposal);

        log.debug("Proposal created with ID={}", result.getId());
        return Response.status(Response.Status.CREATED).entity(orderProposalMapper.toDto(result)).build();
    }

    /**
     * Retrieves all proposals associated with a specific order.
     *
     * @param orderId    the ID of the order
     * @param pagination the pagination parameters (page and size)
     * @return list of proposals for the order
     */
    @GET
    @Path("/{orderId}/proposals")
    @RolesAllowed("CLIENT")
    public Response getProposals(
            @PathParam("orderId") final Long orderId,
            @BeanParam final PageRequest pagination
    ) {
        log.info("Get all proposals for orderId={}", orderId);
        List<OrderProposal> proposals = orderProposalService.getByOrderId(
                orderId,
                pagination.getPage(),
                pagination.getSize());
        log.debug("Proposals found: {}", proposals.size());
        return Response.ok(orderProposalMapper.toDtoList(proposals)).build();
    }


    /**
     * Confirms a proposal and updates the corresponding order with final price and deadline.
     * <p>
     * Only the client who created the order can confirm a proposal.
     * </p>
     *
     * @param orderId the ID of the proposal to confirm
     * @param confirm the confirmation data including final price and deadline
     * @return the updated and confirmed order
     */
    @POST
    @Path("/{orderId}/confirm")
    @RolesAllowed("CLIENT")
    public Response confirm(@PathParam("orderId") final Long orderId,
                            @Valid final ConfirmProposal confirm) {
        log.info("Confirm order proposal: orderId ={},confirm={}", orderId, confirm);

        Order result = orderService.confirm(
                orderId,
                confirm.getProposalId(),
                confirm.getFinalPrice(),
                confirm.getFinalDeadline());
        log.debug("Order confirmed with ID={}", result.getId());
        return Response.ok(orderMapper.toDto(result)).build();
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
     * @param criteria   the filtering criteria for searching orders
     * @param pagination the pagination parameters (page and size)
     * @return list of all orders
     */
    @GET
    @PermitAll
    public Response getOrders(
            @BeanParam final OrderFilterCriteria criteria,
            @BeanParam final PageRequest pagination) {

        if (criteria.hasFilters()) {
            var searchResults = orderSearchService.search(
                    criteria.getQuery(),
                    criteria.getLocation(),
                    criteria.getServices(),
                    pagination.getPage(),
                    pagination.getSize()
            );

            List<Order> result = orderSearchMapper.toEntityList(searchResults);

            return Response.ok(orderMapper.toDtoList(result)).build();
        }

        log.info("Get all orders");
        List<Order> result = orderService.getAll(
                pagination.getPage(),
                pagination.getSize()
        );
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

}
