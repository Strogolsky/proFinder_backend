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
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Path("/specialist")
@Slf4j
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
        log.info("Get all specialists");
        List<Specialist> result = specialistService.getAll();
        log.debug("Found {} specialists", result.size());
        return Response.ok(specialistMapper.toDtoList(result)).build();
    }

    @GET
    @Path("/{id}")
    @PermitAll
    public Response getById(@PathParam("id") Long id) {
        log.info("Get specialist by ID={}", id);
        Specialist result = specialistService.getById(id);
        return Response.ok(specialistMapper.toDto(result)).build();
    }

    @POST
    @DenyAll
    public Response create(@Valid SpecialistDto dto) {
        log.info("Create specialist request: {}", dto);
        Specialist entity = specialistMapper.toEntity(dto);
        try {
            Specialist result = specialistService.create(entity);
            log.debug("Specialist created with ID={}", result.getId());
            return Response.ok(specialistMapper.toDto(result)).build();
        } catch (IllegalArgumentException e) {
            log.error("Error creating specialist: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    @DenyAll
    public Response update(@PathParam("id") Long id, @Valid SpecialistDto dto) {
        log.info("Update specialist request: ID={}, dto={}", id, dto);
        Specialist entity = specialistMapper.toEntity(dto);
        Specialist result = specialistService.update(id, entity);
        log.debug("Specialist updated with ID={}", result.getId());
        return Response.ok(specialistMapper.toDto(result)).build();
    }

    @DELETE
    @Path("/{id}")
    @DenyAll
    public Response delete(@PathParam("id") Long id) {
        log.info("Delete specialist request: ID={}", id);
        specialistService.delete(id);
        return Response.ok().build();
    }

    @GET
    @Path("/me")
    @RolesAllowed("SPECIALIST")
    public Response getProfile() {
        Long specialistId = authService.getCurrentUserId();
        log.info("Get profile for specialist ID={}", specialistId);
        Specialist specialist = specialistService.getById(specialistId);
        return Response.ok(specialistMapper.toDto(specialist)).build();
    }

    @PUT
    @Path("/me")
    @RolesAllowed("SPECIALIST")
    public Response updateProfile(@Valid SpecialistDto dto) {
        Long id = authService.getCurrentUserId();
        log.info("Update profile for specialist ID={} with dto={}", id, dto);
        Specialist specialist = specialistService.update(id, specialistMapper.toEntity(dto));
        log.debug("Specialist profile updated, ID={}", specialist.getId());
        return Response.ok(specialistMapper.toDto(specialist)).build();
    }

    @DELETE
    @Path("/me")
    @RolesAllowed("SPECIALIST")
    public Response deleteProfile() {
        Long id = authService.getCurrentUserId();
        log.info("Delete profile for specialist ID={}", id);
        specialistService.delete(id);
        return Response.ok().build();
    }
}
