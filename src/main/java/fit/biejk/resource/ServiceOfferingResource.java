package fit.biejk.resource;

import fit.biejk.entity.ServiceOffering;
import fit.biejk.service.ServiceOfferingService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/service")
public class ServiceOfferingResource {

    @Inject
    private ServiceOfferingService serviceOfferingService;

    @GET
    @PermitAll
    public Response getAll() {
        List<ServiceOffering> serviceOfferings = serviceOfferingService.getAll();
        return Response.ok(serviceOfferings).build();
    }


}
