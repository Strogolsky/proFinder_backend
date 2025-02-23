package fit.biejk.resource;

import fit.biejk.entity.Specialist;
import fit.biejk.service.SpecialistService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/specialist")
public class SpecialistResource {
    @Inject
    SpecialistService specialistService;

    @GET
    public Response getAll() {
        List<Specialist> result = specialistService.getAll();
        return Response.ok(result).build();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        return Response.ok(specialistService.getById(id)).build();

    }

    @POST
    public Response create(Specialist specialist) {
        Specialist result = specialistService.create(specialist);
        return Response.ok(result).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, Specialist specialist) {
        Specialist result = specialistService.update(id, specialist);
        return Response.ok(result).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        specialistService.delete(id);
        return Response.ok().build();
    }



}
