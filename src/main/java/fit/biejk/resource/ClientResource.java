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
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Path("/client")
@Slf4j
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
        log.info("getAll request");
        List<Client> result = clientService.getAll();
        log.debug("Found {} clients", result.size());
        return Response.ok(clientMapper.toDtoList(result)).build();
    }

    @GET
    @Path("/{id}")
    @PermitAll
    public Response getById(@PathParam("id") Long id) {
        log.info("getById request: {}", id);
        Client result = clientService.getById(id);
        log.debug("Found client with ID={}", result.getId());
        return Response.ok(clientMapper.toDto(result)).build();
    }

    @POST
    @DenyAll
    public Response create(@Valid ClientDto dto) {
        log.info("create request: {}", dto);
        Client entity = clientMapper.toEntity(dto);
        try {
            Client result = clientService.create(entity);
            log.debug("Created client with ID={}", result.getId());
            return Response.ok(clientMapper.toDto(result)).build();
        } catch (IllegalArgumentException e) {
            log.error("Create failed: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    @DenyAll
    public Response update(@PathParam("id") Long id, @Valid ClientDto dto) {
        log.info("update request: id={}, dto={}", id, dto);
        Client entity = clientMapper.toEntity(dto);
        Client result = clientService.update(id, entity);
        log.debug("Updated client with ID={}", result.getId());
        return Response.ok(clientMapper.toDto(result)).build();
    }

    @DELETE
    @Path("/{id}")
    @DenyAll
    public Response delete(@PathParam("id") Long id) {
        log.info("delete request: {}", id);
        clientService.delete(id);
        log.debug("Deleted client with ID={}", id);
        return Response.ok().build();
    }

    @GET
    @Path("/me")
    @RolesAllowed("CLIENT")
    public Response getProfile() {
        log.info("getProfile request");
        Long clientId = authService.getCurrentUserId();
        Client client = clientService.getById(clientId);
        log.debug("Profile client ID={}", clientId);
        return Response.ok(clientMapper.toDto(client)).build();
    }

    @PUT
    @Path("/me")
    @RolesAllowed("CLIENT")
    public Response updateProfile(@Valid ClientDto dto) {
        log.info("updateProfile request: {}", dto);
        Long id = authService.getCurrentUserId();
        Client client = clientService.update(id, clientMapper.toEntity(dto));
        log.debug("Updated profile for client with ID={}", client.getId());
        return Response.ok(clientMapper.toDto(client)).build();
    }

    @DELETE
    @Path("/me")
    @RolesAllowed("CLIENT")
    public Response deleteProfile() {
        log.info("deleteProfile request");
        Long id = authService.getCurrentUserId();
        clientService.delete(id);
        log.debug("Deleted profile for client with ID={}", id);
        return Response.ok().build();
    }
}
