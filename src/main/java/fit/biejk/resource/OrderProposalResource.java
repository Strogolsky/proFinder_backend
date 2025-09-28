package fit.biejk.resource;

import fit.biejk.dto.ConfirmProposal;
import fit.biejk.entity.Order;
import fit.biejk.entity.OrderProposal;
import fit.biejk.mapper.OrderMapper;
import fit.biejk.mapper.OrderProposalMapper;
import fit.biejk.service.OrderProposalService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;

import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * REST resource for managing order proposals submitted by specialists.
 * <p>
 * This resource allows retrieval of proposals by specialist, by proposal ID, or by order ID.
 * It also supports confirming proposals by the client and fetching related order data.
 * </p>
 */
@Slf4j
@Path("/v1/proposals")
public class OrderProposalResource {

    /**
     * Service responsible for handling business logic related to order proposals.
     */
    @Inject
    private OrderProposalService orderProposalService;

    /**
     * Mapper for converting between OrderProposal entities and DTOs.
     */
    @Inject
    private OrderProposalMapper orderProposalMapper;

    /**
     * Mapper for converting between Order entities and DTOs.
     */
    @Inject
    private OrderMapper orderMapper;

    /**
     * Retrieves all proposals submitted by the specified specialist.
     *
     * @param specialistId the ID of the specialist
     * @return list of proposals submitted by the specialist
     */
    @GET
    @Path("/specialist/{specialistId}")
    @RolesAllowed("SPECIALIST")
    public Response getBySpecialistId(@PathParam("specialistId") final Long specialistId) {
        List<OrderProposal> result = orderProposalService.getBySpecialistId(specialistId);
        return Response.ok(orderProposalMapper.toDtoList(result)).build();
    }

    /**
     * Retrieves the order associated with a specific proposal.
     *
     * @param proposalId the ID of the proposal
     * @return order associated with the proposal
     */
    @GET
    @Path("/{proposalId}/orders")
    @RolesAllowed({"CLIENT", "SPECIALIST"})
    public Response getOrderById(@PathParam("proposalId") final Long proposalId) {
        Order order = orderProposalService.getOrderById(proposalId);
        return Response.ok(orderMapper.toDto(order)).build();
    }

    /**
     * Retrieves a specific proposal by its ID.
     *
     * @param proposalId the ID of the proposal
     * @return the corresponding proposal
     */
    @GET
    @Path("/{proposalId}")
    @RolesAllowed({"CLIENT", "SPECIALIST"})
    public Response getById(@PathParam("proposalId") final Long proposalId) {
        log.info("Get proposal: proposalId={}", proposalId);
        OrderProposal result = orderProposalService.getById(proposalId);
        return Response.ok(orderProposalMapper.toDto(result)).build();
    }

    /**
     * Retrieves all proposals associated with a specific order.
     *
     * @param orderId the ID of the order
     * @return list of proposals for the order
     */
    @GET
    @Path("/order/{orderId}")
    @RolesAllowed("CLIENT")
    public Response getAllByOrderId(@PathParam("orderId") final Long orderId) {
        log.info("Get all proposals for orderId={}", orderId);
        List<OrderProposal> proposals = orderProposalService.getByOrderId(orderId);
        log.debug("Proposals found: {}", proposals.size());
        return Response.ok(orderProposalMapper.toDtoList(proposals)).build();
    }

    /**
     * Confirms a proposal and updates the corresponding order with final price and deadline.
     * <p>
     * Only the client who created the order can confirm a proposal.
     * </p>
     *
     * @param proposalId the ID of the proposal to confirm
     * @param confirm the confirmation data including final price and deadline
     * @return the updated and confirmed order
     */
    @POST
    @Path("/{proposalId}:confirm")
    @RolesAllowed("CLIENT")
    public Response confirm(@PathParam("proposalId") final Long proposalId,
                            @Valid final ConfirmProposal confirm) {
        log.info("Confirm proposal: proposalId={}, confirm={}", proposalId, confirm);

        Order result = orderProposalService.confirm(proposalId,
                confirm.getFinalPrice(), confirm.getFinalDeadline());
        log.debug("Order confirmed with ID={}", result.getId());
        return Response.ok(orderMapper.toDto(result)).build();
    }
}
