package fit.biejk.resource;

import fit.biejk.entity.Client;
import fit.biejk.service.ClientService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/client")
public class ClientResource {

    @Inject
    ClientService clientService;

    @GET
    public Response getAll() {
        List<Client> result = clientService.getAll();
        return Response.ok(result).build();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        return Response.ok(clientService.getById(id)).build();

    }

    @POST
    public Response create(Client client) {
        Client result = clientService.create(client);
        return Response.ok(result).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, Client client) {
        Client result = clientService.update(id, client);
        return Response.ok(result).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        clientService.delete(id);
        return Response.ok().build();
    }
}
