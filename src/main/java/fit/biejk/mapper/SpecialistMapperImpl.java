package fit.biejk.mapper;

import fit.biejk.dto.SpecialistDto;
import fit.biejk.entity.Specialist;
import fit.biejk.minIO.UserFileService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link SpecialistMapper}.
 * <p>
 * Responsible for converting between {@link Specialist} entities and {@link SpecialistDto} DTOs.
 * Includes logic for embedding avatar URLs from object storage.
 * </p>
 */
@ApplicationScoped
@Slf4j
public class SpecialistMapperImpl implements SpecialistMapper {

    /**
     * Service to generate presigned URLs for avatars stored in MinIO.
     */
    @Inject
    private UserFileService userFileService;

    /**
     * Converts a {@link Specialist} entity to a {@link SpecialistDto}.
     *
     * @param entity the Specialist entity
     * @return the mapped DTO, or null if entity is null
     */
    @Override
    public SpecialistDto toDto(final Specialist entity) {
        if (entity == null) {
            return null;
        }

        log.debug("Mapping Specialist entity to DTO: id={}", entity.getId());

        SpecialistDto dto = new SpecialistDto();
        dto.setId(entity.getId());
        dto.setEmail(entity.getEmail());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setPhoneNumber(entity.getPhoneNumber());
        dto.setCreateAt(entity.getCreateAt());
        dto.setLocation(entity.getLocation());
        dto.setAverageRating(entity.getAverageRating());
        dto.setDescription(entity.getDescription());

        if (entity.getServiceOfferings() != null) {
            dto.setServiceOfferings(new ArrayList<>(entity.getServiceOfferings()));
        }

//        if (entity.getAvatarKey() != null) {
//            try {
//                dto.setAvatarUrl(userFileService.getAvatarUrl(entity.getId()));
//            } catch (Exception e) {
//                log.warn("Failed to generate avatar URL for specialistId={}", entity.getId(), e);
//            }
//        }

        return dto;
    }

    /**
     * Converts a {@link SpecialistDto} to a {@link Specialist} entity.
     *
     * @param dto the Specialist DTO
     * @return the mapped entity, or null if dto is null
     */
    @Override
    public Specialist toEntity(final SpecialistDto dto) {
        if (dto == null) {
            return null;
        }

        log.debug("Mapping SpecialistDto to entity: id={}", dto.getId());

        Specialist entity = new Specialist();
        entity.setId(dto.getId());
        entity.setEmail(dto.getEmail());
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setPhoneNumber(dto.getPhoneNumber());
        entity.setLocation(dto.getLocation());
        entity.setDescription(dto.getDescription());
        entity.setAverageRating(dto.getAverageRating());

        if (dto.getServiceOfferings() != null) {
            entity.setServiceOfferings(new ArrayList<>(dto.getServiceOfferings()));
        }

        return entity;
    }

    /**
     * Converts a list of {@link Specialist} entities to a list of {@link SpecialistDto} DTOs.
     *
     * @param entityList the list of entities
     * @return list of mapped DTOs, or null if input is null
     */
    @Override
    public List<SpecialistDto> toDtoList(final List<Specialist> entityList) {
        if (entityList == null) {
            return null;
        }

        log.debug("Mapping list of Specialist entities to DTOs: size={}", entityList.size());

        List<SpecialistDto> dtoList = new ArrayList<>(entityList.size());
        for (Specialist specialist : entityList) {
            dtoList.add(toDto(specialist));
        }

        return dtoList;
    }

    /**
     * Converts a list of {@link SpecialistDto} DTOs to a list of {@link Specialist} entities.
     *
     * @param dtoList the list of DTOs
     * @return list of mapped entities, or null if input is null
     */
    @Override
    public List<Specialist> toEntityList(final List<SpecialistDto> dtoList) {
        if (dtoList == null) {
            return null;
        }

        log.debug("Mapping list of Specialist DTOs to entities: size={}", dtoList.size());

        List<Specialist> entityList = new ArrayList<>(dtoList.size());
        for (SpecialistDto dto : dtoList) {
            entityList.add(toEntity(dto));
        }

        return entityList;
    }
}
