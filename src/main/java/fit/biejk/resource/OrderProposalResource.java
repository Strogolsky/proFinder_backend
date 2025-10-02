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
}
