package fit.biejk.resource;

import fit.biejk.dto.OrderDto;
import fit.biejk.dto.OrderProposalDto;
import fit.biejk.entity.Client;
import fit.biejk.entity.Order;
import fit.biejk.entity.OrderProposal;
import fit.biejk.entity.Specialist;
import fit.biejk.mapper.OrderMapper;
import fit.biejk.mapper.OrderProposalMapper;
import fit.biejk.service.ClientService;
import fit.biejk.service.OrderService;
import fit.biejk.service.SpecialistService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
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
    OrderProposalMapper orderProposalMapper;

    @POST
    public Response create(@Valid OrderDto dto) {
        Order order = orderMapper.toEntity(dto);

        Client client = clientService.getById(dto.getClientId());
        order.setClient(client);
//        if (dto.getSpecialistId() != null) {
//            Specialist specialist = specialistService.getById(dto.getSpecialistId());
//            order.setSpecialist(specialist);
//        }
        Order result = orderService.create(order);
        return Response.ok(orderMapper.toDto(result)).build();
    }

    @PUT
    @Path("/{orderId}/cancel")
    public Response cancel(Long orderId) {
        Order result = orderService.cancel(orderId);
        return Response.ok(orderMapper.toDto(result)).build();
    }

    @PUT
    @Path("/{orderId}/proposal")
    public Response proposal(Long orderId, @Valid OrderProposalDto proposalDto) {
        OrderProposal proposal = orderProposalMapper.toEntity(proposalDto);
        Specialist specialist = specialistService.getById(proposalDto.getSpecialistId());
        proposal.setSpecialist(specialist);


        OrderProposal result = orderService.proposal(orderId, proposal);

        return Response.ok(orderProposalMapper.toDto(result)).build();
    }

    @GET
    @Path("/{orderId}")
    public Response getById(Long orderId) {
        Order result = orderService.getById(orderId);
        return Response.ok(orderMapper.toDto(result)).build();
    }

    @GET
    public Response getAll() {
        List<Order> result = orderService.getAll();
        return Response.ok(orderMapper.toDtoList(result)).build();
    }

    @GET
    @Path("/{orderId}/proposal")
    public Response getProposal(Long orderId) {
        List<OrderProposal> proposals = orderService.getOrderProposals(orderId);
        return Response.ok(orderProposalMapper.toDtoList(proposals)).build();
    }
}
