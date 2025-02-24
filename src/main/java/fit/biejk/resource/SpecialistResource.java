package fit.biejk.resource;

import fit.biejk.dto.SpecialistDto;
import fit.biejk.entity.Specialist;
import fit.biejk.mapper.SpecialistMapper;
import fit.biejk.service.SpecialistService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/specialist")
public class SpecialistResource {
    @Inject
    SpecialistService specialistService;
    @Inject
    SpecialistMapper specialistMapper;

    @GET
    public Response getAll() {
        List<Specialist> result = specialistService.getAll();
        return Response.ok(specialistMapper.toDtoList(result)).build();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Specialist result = specialistService.getById(id);
        return Response.ok(specialistMapper.toDto(result)).build();

    }

    @POST
    public Response create(SpecialistDto dto) {
        Specialist entity = specialistMapper.toEntity(dto);
        Specialist result = specialistService.create(entity);
        return Response.ok(specialistMapper.toDto(result)).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, SpecialistDto dto) {
        Specialist entity = specialistMapper.toEntity(dto);
        Specialist result = specialistService.update(id, entity);
        return Response.ok(specialistMapper.toDto(result)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        specialistService.delete(id);
        return Response.ok().build();
    }



}
