package fit.biejk.search;

import fit.biejk.entity.Specialist;
import fit.biejk.mapper.SpecialistMapper;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/search")
public class SearchResource {
    @Inject
    SpecialistSearchService specialistSearchService;

    @Inject
    SpecialistSearchMapper specialistSearchMapper;

    @Inject
    SpecialistMapper specialistMapper;

    @GET
    @Path("/specialist")
    @PermitAll
    public Response search(@QueryParam("query") String query,
                           @QueryParam("location") String location) {

        List<SpecialistSearchDto> results = specialistSearchService.search(query, location);
        List<Specialist> specialists = specialistSearchMapper.toEntityList(results);

        return Response.ok(specialistMapper.toDtoList(specialists)).build();
    }

}
