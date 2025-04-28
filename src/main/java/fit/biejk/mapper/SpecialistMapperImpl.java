package fit.biejk.mapper;

import fit.biejk.dto.SpecialistDto;
import fit.biejk.entity.Specialist;
import fit.biejk.minIO.UserFileService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@Slf4j
public class SpecialistMapperImpl implements SpecialistMapper {

    @Inject
    UserFileService userFileService;

    @Override
    public SpecialistDto toDto(Specialist entity) {
        if (entity == null) {
            return null;
        }

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

        if (entity.getAvatarKey() != null) {
            try {
                dto.setAvatarUrl(userFileService.getAvatarUrl(entity.getId()));
            } catch (Exception e) {
                log.warn("Failed to generate avatar URL for specialistId={}", entity.getId(), e);
            }
        }

        return dto;
    }

    @Override
    public Specialist toEntity(SpecialistDto dto) {
        if (dto == null) {
            return null;
        }

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

    @Override
    public List<SpecialistDto> toDtoList(List<Specialist> entityList) {
        if (entityList == null) {
            return null;
        }

        List<SpecialistDto> dtoList = new ArrayList<>(entityList.size());
        for (Specialist specialist : entityList) {
            dtoList.add(toDto(specialist));
        }

        return dtoList;
    }

    @Override
    public List<Specialist> toEntityList(List<SpecialistDto> dtoList) {
        if (dtoList == null) {
            return null;
        }

        List<Specialist> entityList = new ArrayList<>(dtoList.size());
        for (SpecialistDto dto : dtoList) {
            entityList.add(toEntity(dto));
        }

        return entityList;
    }
}

