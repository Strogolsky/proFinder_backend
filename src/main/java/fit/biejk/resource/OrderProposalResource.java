package fit.biejk.resource;

import fit.biejk.entity.Order;
import fit.biejk.entity.OrderProposal;
import fit.biejk.mapper.OrderMapper;
import fit.biejk.mapper.OrderProposalMapper;
import fit.biejk.service.OrderProposalService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import jakarta.ws.rs.QueryParam;
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
    public Response getBySpecialistId(@PathParam("specialistId") final Long specialistId) {
        List<OrderProposal> result = orderProposalService.getBySpecialistId(specialistId);
        return Response.ok(orderProposalMapper.toDtoList(result)).build();
    }

    @GET
    @Path("/{proposalId}/order")
    public Response getOrderById(@PathParam("proposalId") final Long proposalId) {
        Order order = orderProposalService.getOrderById(proposalId);
        return Response.ok(orderMapper.toDto(order)).build();
    }


}
