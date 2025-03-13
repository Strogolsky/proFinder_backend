package fit.biejk.resource;

import fit.biejk.dto.ClientDto;
import fit.biejk.entity.Client;
import fit.biejk.mapper.ClientMapper;
import fit.biejk.service.AuthService;
import fit.biejk.service.ClientService;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/client")
public class ClientResource {

    @Inject
    ClientMapper clientMapper;

    @Inject
    ClientService clientService;
    @Inject
    AuthService authService;

    @GET
    @PermitAll
    public Response getAll() {
        List<Client> result = clientService.getAll();
        return Response.ok(clientMapper.toDtoList(result)).build();
    }

    @GET
    @Path("/{id}")
    @PermitAll
    public Response getById(@PathParam("id") Long id) {
        Client result = clientService.getById(id);
        return Response.ok(clientMapper.toDto(result)).build();

    }

    @POST
    @DenyAll
    public Response create(@Valid ClientDto dto) {
        Client entity = clientMapper.toEntity(dto);
        try {
            Client result = clientService.create(entity);
            return Response.ok(clientMapper.toDto(result)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }

    }

    @PUT
    @Path("/{id}")
    @DenyAll
    public Response update(@PathParam("id") Long id, @Valid ClientDto dto) {
        Client entity = clientMapper.toEntity(dto);
        Client result = clientService.update(id, entity);
        return Response.ok(clientMapper.toDto(result)).build();
    }

    @DELETE
    @Path("/{id}")
    @DenyAll
    public Response delete(@PathParam("id") Long id) {
        clientService.delete(id);
        return Response.ok().build();
    }

    @GET
    @Path("/me")
    @RolesAllowed("CLIENT")
    public Response getProfile() {
        Long clientId = authService.getCurrentUserId();
        Client client = clientService.getById(clientId);
        return Response.ok(clientMapper.toDto(client)).build();
    }

    @PUT
    @Path("/me")
    @RolesAllowed("CLIENT")
    public Response updateProfile(@Valid ClientDto dto) {
        Long id = authService.getCurrentUserId();
        Client client = clientService.update(id, clientMapper.toEntity(dto));
        return Response.ok(clientMapper.toDto(client)).build();
    }

    @DELETE
    @Path("/me")
    @RolesAllowed("CLIENT")
    public Response deleteProfile() {
        Long id = authService.getCurrentUserId();
        clientService.delete(id);
        return Response.ok().build();
    }


}
