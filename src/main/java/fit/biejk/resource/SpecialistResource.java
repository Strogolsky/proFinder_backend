package fit.biejk.resource;

import fit.biejk.dto.SpecialistDto;
import fit.biejk.entity.Specialist;
import fit.biejk.mapper.SpecialistMapper;
import fit.biejk.service.AuthService;
import fit.biejk.service.SpecialistService;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/specialist")
public class SpecialistResource {
    @Inject
    SpecialistService specialistService;
    @Inject
    SpecialistMapper specialistMapper;

    @Inject
    AuthService authService;

    @GET
    @PermitAll
    public Response getAll() {
        List<Specialist> result = specialistService.getAll();
        return Response.ok(specialistMapper.toDtoList(result)).build();
    }

    @GET
    @Path("/{id}")
    @PermitAll
    public Response getById(@PathParam("id") Long id) {
        Specialist result = specialistService.getById(id);
        return Response.ok(specialistMapper.toDto(result)).build();

    }

    @POST
    @DenyAll
    public Response create(@Valid SpecialistDto dto) {
        Specialist entity = specialistMapper.toEntity(dto);
        try {
            Specialist result = specialistService.create(entity);
            return Response.ok(specialistMapper.toDto(result)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }

    }

    @PUT
    @Path("/{id}")
    @DenyAll
    public Response update(@PathParam("id") Long id, @Valid SpecialistDto dto) {
        Specialist entity = specialistMapper.toEntity(dto);
        Specialist result = specialistService.update(id, entity);
        return Response.ok(specialistMapper.toDto(result)).build();
    }

    @DELETE
    @Path("/{id}")
    @DenyAll
    public Response delete(@PathParam("id") Long id) {
        specialistService.delete(id);
        return Response.ok().build();
    }

    @GET
    @Path("/me")
    @RolesAllowed("SPECIALIST")
    public Response getProfile() {
        Long clientId = authService.getCurrentUserId();
        Specialist specialist = specialistService.getById(clientId);
        return Response.ok(specialistMapper.toDto(specialist)).build();
    }

    @PUT
    @Path("/me")
    @RolesAllowed("SPECIALIST")
    public Response updateProfile(@Valid SpecialistDto dto) {
        Long id = authService.getCurrentUserId();
        Specialist specialist = specialistService.update(id, specialistMapper.toEntity(dto));
        return Response.ok(specialistMapper.toDto(specialist)).build();
    }

    @DELETE
    @Path("/me")
    @RolesAllowed("SPECIALIST")
    public Response deleteProfile() {
        Long id = authService.getCurrentUserId();
        specialistService.delete(id);
        return Response.ok().build();
    }



}
