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
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Path("/proposal")
public class OrderProposalResource {

    @Inject
    OrderProposalService orderProposalService;

    @Inject
    OrderProposalMapper orderProposalMapper;

    @Inject
    OrderMapper orderMapper;

    @GET
    @Path("/specialist/{specialistId}")
    @RolesAllowed("SPECIALIST")
    public Response getBySpecialistId(@PathParam("specialistId") final Long specialistId) {
        List<OrderProposal> result = orderProposalService.getBySpecialistId(specialistId);
        return Response.ok(orderProposalMapper.toDtoList(result)).build();
    }

    @GET
    @Path("/{proposalId}/order")
    @RolesAllowed({"CLIENT", "SPECIALIST"})
    public Response getOrderById(@PathParam("proposalId") final Long proposalId) {
        Order order = orderProposalService.getOrderById(proposalId);
        return Response.ok(orderMapper.toDto(order)).build();
    }

    @GET
    @Path("/{proposalId}")
    @RolesAllowed({"CLIENT", "SPECIALIST"})
    public Response getById(@PathParam("proposalId") final Long proposalId) {
        log.info("Get proposal: proposalId={}", proposalId);
        OrderProposal result = orderProposalService.getById(proposalId);
        return Response.ok(orderProposalMapper.toDto(result)).build();
    }

    @GET
    @Path("/order/{orderId}")
    @RolesAllowed("CLIENT")
    public Response getAllByOrderId(@PathParam("orderId") final Long orderId) {
        log.info("Get all proposals for orderId={}", orderId);
        List<OrderProposal> proposals = orderProposalService.getByOrderId(orderId);
        log.debug("Proposals found: {}", proposals.size());
        return Response.ok(orderProposalMapper.toDtoList(proposals)).build();
    }

    @PUT
    @Path("/{proposalId}/confirm")
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
