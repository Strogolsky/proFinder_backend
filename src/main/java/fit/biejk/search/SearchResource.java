package fit.biejk.search;

import fit.biejk.entity.Order;
import fit.biejk.entity.Specialist;
import fit.biejk.mapper.OrderMapper;
import fit.biejk.mapper.SpecialistMapper;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

import java.util.List;

/**
 * REST endpoint for searching specialists.
 */
@Path("/search")
public class SearchResource {

    /**
     * Service responsible for interacting with Elasticsearch.
     */
    @Inject
    private SpecialistSearchService specialistSearchService;

    /**
     * Mapper for converting between Specialist and SpecialistSearchDto.
     */
    @Inject
    private SpecialistSearchMapper specialistSearchMapper;

    /**
     * Mapper for converting between Specialist entity and DTOs.
     */
    @Inject
    private SpecialistMapper specialistMapper;

    @Inject
    OrderSearchMapper orderSearchMapper;

    @Inject
    OrderMapper orderMapper;

    @Inject
    OrderSearchService orderSearchService;

    /**
     * Searches for specialists based on a text query and location.
     * Returns mapped DTOs for use in API response.
     *
     * @param query    the search keyword (e.g., service name or description)
     * @param location the city to filter specialists by
     * @return HTTP response containing the list of matching specialist DTOs
     */
    @GET
    @Path("/specialist")
    @PermitAll
    public Response searchSpecialists(@QueryParam("query") final String query,
                           @QueryParam("location") final String location) {

        List<SpecialistSearchDto> results = specialistSearchService.search(query, location);
        List<Specialist> specialists = specialistSearchMapper.toEntityList(results);

        return Response.ok(specialistMapper.toDtoList(specialists)).build();
    }

    @GET
    @Path("/order")
    @PermitAll
    public Response searchOrders(final List<String> services, @QueryParam("location") final String location) {
        List<OrderSearchDto> results = orderSearchService.search(services, location);
        List<Order> orders = orderSearchMapper.toEntityList(results);

        return Response.ok(orderMapper.toDtoList(orders)).build();
    }

}
