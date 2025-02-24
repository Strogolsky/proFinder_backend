package fit.biejk.resource;

import fit.biejk.dto.ClientDto;
import fit.biejk.entity.Client;
import fit.biejk.mapper.ClientMapper;
import fit.biejk.service.ClientService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/client")
public class ClientResource {

    @Inject
    ClientMapper clientMapper;

    @Inject
    ClientService clientService;

    @GET
    public Response getAll() {
        List<Client> result = clientService.getAll();
        return Response.ok(clientMapper.toDtoList(result)).build();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Client result = clientService.getById(id);
        return Response.ok(clientMapper.toDto(result)).build();

    }

    @POST
    public Response create(ClientDto dto) {
        Client entity = clientMapper.toEntity(dto);
        Client result = clientService.create(entity);
        return Response.ok(clientMapper.toDto(result)).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, ClientDto dto) {
        Client entity = clientMapper.toEntity(dto);
        Client result = clientService.update(id, entity);
        return Response.ok(clientMapper.toDto(result)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        clientService.delete(id);
        return Response.ok().build();
    }
}
