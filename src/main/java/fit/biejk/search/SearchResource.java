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
 * REST resource for handling search operations for specialists and orders.
 * <p>
 * Provides endpoints to search specialists based on a keyword and location,
 * and to search orders based on selected services and location.
 * </p>
 */
@Path("/v1/search")
public class SearchResource {

    /**
     * Service responsible for performing specialist-related Elasticsearch queries.
     */
    @Inject
    private SpecialistSearchService specialistSearchService;

    /**
     * Mapper for converting between {@link SpecialistSearchDto} and {@link Specialist}.
     */
    @Inject
    private SpecialistSearchMapper specialistSearchMapper;

    /**
     * Mapper for converting between {@link Specialist} and its DTO representation.
     */
    @Inject
    private SpecialistMapper specialistMapper;

    /**
     * Mapper for converting between {@link OrderSearchDto} and {@link Order}.
     */
    @Inject
    private OrderSearchMapper orderSearchMapper;

    /**
     * Mapper for converting between {@link Order} and its DTO representation.
     */
    @Inject
    private OrderMapper orderMapper;

    /**
     * Service responsible for performing order-related Elasticsearch queries.
     */
    @Inject
    private OrderSearchService orderSearchService;

    /**
     * Searches for specialists based on a text query and a given location.
     * <p>
     * The query can match service names or specialist descriptions. The location is used as a filter.
     * </p>
     *
     * @param query    the search keyword (e.g., service name, skill)
     * @param location the location to filter specialists by
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

    /**
     * Searches for orders based on selected services and location.
     * <p>
     * Returns only orders that are in status "CREATED" or "CLIENT_PENDING".
     * </p>
     *
     * @param services list of service names to search for
     * @param location the location to filter orders by
     * @return HTTP response containing the list of matching order DTOs
     */
    @GET
    @Path("/order")
    @PermitAll
    public Response searchOrders(final List<String> services,
                                 @QueryParam("location") final String location) {
        List<OrderSearchDto> results = orderSearchService.search(services, location);
        List<Order> orders = orderSearchMapper.toEntityList(results);
        return Response.ok(orderMapper.toDtoList(orders)).build();
    }
}
