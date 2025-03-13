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
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/order")
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
        Order order = orderMapper.toEntity(dto);

        Long clientId = authService.getCurrentUserId();
        Client client = clientService.getById(clientId);
        order.setClient(client);

        Order result = orderService.create(order);
        return Response.ok(orderMapper.toDto(result)).build();
    }

    @PUT
    @Path("/{orderId}")
    @RolesAllowed("CLIENT")
    public Response update(Long orderId, @Valid OrderDto dto) {
        Order order = orderMapper.toEntity(dto);

        Order result = orderService.update(orderId, order);
        return Response.ok(orderMapper.toDto(result)).build();
    }

    @PUT
    @Path("/{orderId}/cancel")
    @RolesAllowed("CLIENT")
    public Response cancel(Long orderId) {
        Order result = orderService.cancel(orderId);
        return Response.ok(orderMapper.toDto(result)).build();
    }

    @PUT
    @Path("/{orderId}/proposal")
    @RolesAllowed("SPECIALIST")
    public Response proposal(Long orderId, @Valid OrderProposalDto proposalDto) {
        OrderProposal proposal = orderProposalMapper.toEntity(proposalDto);

        Long specialistId = authService.getCurrentUserId();
        Specialist specialist = specialistService.getById(specialistId);
        proposal.setSpecialist(specialist);

        OrderProposal result = orderService.proposal(orderId, proposal);

        return Response.ok(orderProposalMapper.toDto(result)).build();
    }
    @GET
    @Path("/{orderId}")
    @PermitAll
    public Response getById(Long orderId) {
        Order result = orderService.getById(orderId);
        return Response.ok(orderMapper.toDto(result)).build();
    }

    @GET
    @PermitAll
    public Response getAll() {
        List<Order> result = orderService.getAll();
        return Response.ok(orderMapper.toDtoList(result)).build();
    }


    @GET
    @Path("/{orderId}/proposal")
    @RolesAllowed("CLIENT")
    public Response getAllProposal(Long orderId) {
        List<OrderProposal> proposals = orderService.getOrderProposals(orderId);
        return Response.ok(orderProposalMapper.toDtoList(proposals)).build();
    }

    @GET
    @Path("/{orderId}/proposal/{proposalId}")
    @RolesAllowed({"CLIENT", "SPECIALIST"})
    public Response getProposal(Long orderId, Long proposalId) {
        OrderProposal result = orderService.getOrderProposalById(orderId, proposalId);
        return Response.ok(orderProposalMapper.toDto(result)).build();
    }

    @PUT
    @Path("/{orderId}/proposal/{proposalId}/confirm")
    @RolesAllowed("CLIENT")
    public Response confirm(Long orderId, Long proposalId, @Valid ConfirmProposal confirm) {
        Order result = orderService.confirm(orderId,proposalId,confirm.getFinalPrice(), confirm.getFinalDeadline());

        return Response.ok(orderMapper.toDto(result)).build();
    }

    @DELETE
    @Path("/{orderId}")
    @RolesAllowed("CLIENT")
    public Response delete(Long orderId) {
        orderService.delete(orderId);
        return Response.ok().build();
    }
}
