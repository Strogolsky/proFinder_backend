package fit.biejk.resource;

import fit.biejk.dto.ConfirmProposal;
import fit.biejk.dto.OrderDto;
import fit.biejk.dto.OrderProposalDto;
import fit.biejk.entity.Client;
import fit.biejk.entity.Order;
import fit.biejk.entity.OrderProposal;
import fit.biejk.entity.Specialist;
import fit.biejk.mapper.OrderMapper;
import fit.biejk.mapper.OrderProposalMapper;
import fit.biejk.service.AuthService;
import fit.biejk.service.ClientService;
import fit.biejk.service.OrderService;
import fit.biejk.service.SpecialistService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Path("/order")
@Slf4j
public class OrderResource {

    @Inject
    OrderService orderService;

    @Inject
    OrderMapper orderMapper;

    @Inject
    ClientService clientService;

    @Inject
    SpecialistService specialistService;

    @Inject
    AuthService authService;

    @Inject
    OrderProposalMapper orderProposalMapper;

    @POST
    @RolesAllowed("CLIENT")
    public Response create(@Valid OrderDto dto) {
        log.info("Create order request: {}", dto);
        Order order = orderMapper.toEntity(dto);
        Long clientId = authService.getCurrentUserId();
        Client client = clientService.getById(clientId);
        order.setClient(client);
        Order result = orderService.create(order);
        log.debug("Order created with ID={}", result.getId());
        return Response.ok(orderMapper.toDto(result)).build();
    }

    @PUT
    @Path("/{orderId}")
    @RolesAllowed("CLIENT")
    public Response update(@PathParam("orderId") Long orderId, @Valid OrderDto dto) {
        log.info("Update order request: orderId={}, dto={}", orderId, dto);
        Order order = orderMapper.toEntity(dto);
        Order result = orderService.update(orderId, order);
        log.debug("Order updated with ID={}", result.getId());
        return Response.ok(orderMapper.toDto(result)).build();
    }

    @PUT
    @Path("/{orderId}/cancel")
    @RolesAllowed("CLIENT")
    public Response cancel(@PathParam("orderId") Long orderId) {
        log.info("Cancel order request: orderId={}", orderId);
        Order result = orderService.cancel(orderId);
        log.debug("Order canceled with ID={}", result.getId());
        return Response.ok(orderMapper.toDto(result)).build();
    }

    @PUT
    @Path("/{orderId}/proposal")
    @RolesAllowed("SPECIALIST")
    public Response proposal(@PathParam("orderId") Long orderId, @Valid OrderProposalDto proposalDto) {
        log.info("Proposal request: orderId={}, proposalDto={}", orderId, proposalDto);
        OrderProposal proposal = orderProposalMapper.toEntity(proposalDto);
        Long specialistId = authService.getCurrentUserId();
        Specialist specialist = specialistService.getById(specialistId);
        proposal.setSpecialist(specialist);
        OrderProposal result = orderService.proposal(orderId, proposal);
        log.debug("Proposal created with ID={}", result.getId());
        return Response.ok(orderProposalMapper.toDto(result)).build();
    }

    @GET
    @Path("/{orderId}")
    @PermitAll
    public Response getById(@PathParam("orderId") Long orderId) {
        log.info("Get order by ID: {}", orderId);
        Order result = orderService.getById(orderId);
        return Response.ok(orderMapper.toDto(result)).build();
    }

    @GET
    @PermitAll
    public Response getAll() {
        log.info("Get all orders");
        List<Order> result = orderService.getAll();
        log.debug("Orders found: {}", result.size());
        return Response.ok(orderMapper.toDtoList(result)).build();
    }

    @GET
    @Path("/{orderId}/proposal")
    @RolesAllowed("CLIENT")
    public Response getAllProposal(@PathParam("orderId") Long orderId) {
        log.info("Get all proposals for orderId={}", orderId);
        List<OrderProposal> proposals = orderService.getOrderProposals(orderId);
        log.debug("Proposals found: {}", proposals.size());
        return Response.ok(orderProposalMapper.toDtoList(proposals)).build();
    }

    @GET
    @Path("/{orderId}/proposal/{proposalId}")
    @RolesAllowed({"CLIENT", "SPECIALIST"})
    public Response getProposal(@PathParam("orderId") Long orderId, @PathParam("proposalId") Long proposalId) {
        log.info("Get proposal: orderId={}, proposalId={}", orderId, proposalId);
        OrderProposal result = orderService.getOrderProposalById(orderId, proposalId);
        return Response.ok(orderProposalMapper.toDto(result)).build();
    }

    @PUT
    @Path("/{orderId}/proposal/{proposalId}/confirm")
    @RolesAllowed("CLIENT")
    public Response confirm(@PathParam("orderId") Long orderId,
                            @PathParam("proposalId") Long proposalId,
                            @Valid ConfirmProposal confirm) {
        log.info("Confirm proposal: orderId={}, proposalId={}, confirm={}", orderId, proposalId, confirm);
        Order result = orderService.confirm(orderId, proposalId, confirm.getFinalPrice(), confirm.getFinalDeadline());
        log.debug("Order confirmed with ID={}", result.getId());
        return Response.ok(orderMapper.toDto(result)).build();
    }

    @DELETE
    @Path("/{orderId}")
    @RolesAllowed("CLIENT")
    public Response delete(@PathParam("orderId") Long orderId) {
        log.info("Delete order request: orderId={}", orderId);
        orderService.delete(orderId);
        return Response.ok().build();
    }
}
