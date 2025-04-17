package fit.biejk.resource;

import fit.biejk.entity.Location;
import fit.biejk.service.LocationService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import java.util.List;

@Path("/location")
public class LocationResource {
    @Inject
    LocationService locationService;

    @GET
    @PermitAll
    public List<Location> getAll() {
        return locationService.getAll();
    }
}
