package fit.biejk.resource;

import fit.biejk.entity.Client;
import fit.biejk.service.ClientService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
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

    @POST
    public Response create(Client client) {
        Client result = clientService.create(client);
        return Response.ok(result).build();
    }
}
