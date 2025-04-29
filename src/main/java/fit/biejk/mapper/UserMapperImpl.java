package fit.biejk.mapper;

import fit.biejk.dto.UserDto;
import fit.biejk.entity.User;
import fit.biejk.minIO.UserFileService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link UserMapper}.
 * <p>
 * Handles conversion between {@link User} entities and {@link UserDto} DTOs,
 * including generation of avatar URLs from object storage.
 * </p>
 */
@ApplicationScoped
@Slf4j
public class UserMapperImpl implements UserMapper {

    /**
     * Service for generating avatar download URLs.
     */
    @Inject
    private UserFileService userFileService;

    /**
     * Converts a {@link User} entity to a {@link UserDto}.
     *
     * @param entity the User entity
     * @return the corresponding UserDto
     */
    @Override
    public UserDto toDto(final User entity) {
        if (entity == null) {
            return null;
        }

        log.debug("Mapping User entity to DTO: id={}", entity.getId());

        UserDto dto = new UserDto();
        dto.setId(entity.getId());
        dto.setEmail(entity.getEmail());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setPhoneNumber(entity.getPhoneNumber());
        dto.setCreateAt(entity.getCreateAt());
        dto.setLocation(entity.getLocation());

        if (entity.getAvatarKey() != null) {
            try {
                dto.setAvatarUrl(userFileService.getAvatarUrl(entity.getId()));
            } catch (Exception e) {
                log.warn("Failed to generate avatar URL for userId={}", entity.getId(), e);
            }
        }

        return dto;
    }

    /**
     * Converts a {@link UserDto} to a {@link User} entity.
     *
     * @param dto the UserDto
     * @return the corresponding User entity
     */
    @Override
    public User toEntity(final UserDto dto) {
        if (dto == null) {
            return null;
        }

        log.debug("Mapping UserDto to entity: id={}", dto.getId());

        User entity = new User();
        entity.setId(dto.getId());
        entity.setEmail(dto.getEmail());
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setPhoneNumber(dto.getPhoneNumber());
        entity.setLocation(dto.getLocation());
        return entity;
    }

    /**
     * Converts a list of {@link User} entities to a list of {@link UserDto}s.
     *
     * @param entityList the list of User entities
     * @return the list of UserDto objects
     */
    @Override
    public List<UserDto> toDtoList(final List<User> entityList) {
        if (entityList == null) {
            return null;
        }

        log.debug("Mapping list of User entities to DTOs: size={}", entityList.size());

        List<UserDto> dtoList = new ArrayList<>(entityList.size());
        for (User entity : entityList) {
            dtoList.add(toDto(entity));
        }
        return dtoList;
    }

    /**
     * Converts a list of {@link UserDto}s to a list of {@link User} entities.
     *
     * @param dtoList the list of UserDto objects
     * @return the list of User entities
     */
    @Override
    public List<User> toEntityList(final List<UserDto> dtoList) {
        if (dtoList == null) {
            return null;
        }

        log.debug("Mapping list of User DTOs to entities: size={}", dtoList.size());

        List<User> entityList = new ArrayList<>(dtoList.size());
        for (UserDto dto : dtoList) {
            entityList.add(toEntity(dto));
        }
        return entityList;
    }
}
