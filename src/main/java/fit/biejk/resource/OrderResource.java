package fit.biejk.resource;

import fit.biejk.dto.ConfirmProposal;
import fit.biejk.dto.OrderDto;
import fit.biejk.dto.OrderProposalDto;
import fit.biejk.dto.ReviewDto;
import fit.biejk.entity.*;
import fit.biejk.mapper.OrderMapper;
import fit.biejk.mapper.OrderProposalMapper;
import fit.biejk.mapper.ReviewMapper;
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

    @Inject
    ReviewService reviewService;
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

    @Inject
    private ReviewMapper reviewMapper;

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
     * Retrieves all proposals for a specific order.
     *
     * @param orderId order ID
     * @return list of proposals
     */
    @GET
    @Path("/{orderId}/proposal")
    @RolesAllowed("CLIENT")
    public Response getAllProposal(@PathParam("orderId") final Long orderId) {
        log.info("Get all proposals for orderId={}", orderId);
        List<OrderProposal> proposals = orderService.getOrderProposals(orderId);
        log.debug("Proposals found: {}", proposals.size());
        return Response.ok(orderProposalMapper.toDtoList(proposals)).build();
    }

    /**
     * Retrieves a specific proposal for an order.
     *
     * @param orderId    order ID
     * @param proposalId proposal ID
     * @return proposal data
     */
    @GET
    @Path("/{orderId}/proposal/{proposalId}")
    @RolesAllowed({"CLIENT", "SPECIALIST"})
    public Response getProposal(@PathParam("orderId") final Long orderId,
                                @PathParam("proposalId") final Long proposalId) {
        log.info("Get proposal: orderId={}, proposalId={}", orderId, proposalId);
        OrderProposal result = orderService.getOrderProposalById(orderId, proposalId);
        return Response.ok(orderProposalMapper.toDto(result)).build();
    }

    /**
     * Confirms a proposal and finalizes order details.
     *
     * @param orderId     order ID
     * @param proposalId  proposal ID to confirm
     * @param confirm     confirmation data (final price and deadline)
     * @return confirmed order
     */
    @PUT
    @Path("/{orderId}/proposal/{proposalId}/confirm")
    @RolesAllowed("CLIENT")
    public Response confirm(@PathParam("orderId") final Long orderId,
                            @PathParam("proposalId") final Long proposalId,
                            @Valid final ConfirmProposal confirm) {
        log.info("Confirm proposal: orderId={}, proposalId={}, confirm={}", orderId, proposalId, confirm);
        Order result = orderService.confirm(orderId, proposalId, confirm.getFinalPrice(), confirm.getFinalDeadline());
        log.debug("Order confirmed with ID={}", result.getId());
        return Response.ok(orderMapper.toDto(result)).build();
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
     * Submits a review for a completed order.
     * <p>
     * Only the client who created the order can submit the review, and the order must have status COMPLETED.
     * </p>
     *
     * @param orderId the ID of the reviewed order
     * @param dto     the review data
     * @return created review
     */
    @PUT
    @Path("/{orderId}/review")
    @RolesAllowed("CLIENT")
    public Response createReview(@PathParam("orderId") final Long orderId, @Valid final ReviewDto dto) {
        log.info("Review creation request: orderId={}, rating={}, comment={}",
                orderId, dto.getRating(), dto.getComment());

        Review review = reviewMapper.toEntity(dto);
        Review saved = orderService.review(orderId, review);

        log.debug("Review created: reviewId={}, orderId={}, rating={}",
                saved.getId(), orderId, saved.getRating());

        return Response.ok(reviewMapper.toDto(saved)).build();
    }
    @GET
    @Path("/{orderId}/review")
    @RolesAllowed("CLIENT")
    public Response getReview(@PathParam("orderId") final Long orderId) {
        log.info("Get review: orderId={}", orderId);
        Review res = reviewService.getByOrderId(orderId);
        return Response.ok(reviewMapper.toDto(res)).build();
    }

}
