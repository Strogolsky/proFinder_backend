package fit.biejk.mapper;

import fit.biejk.dto.UserDto;
import fit.biejk.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * MapStruct mapper for converting between {@link User}
 * entities and {@link UserDto} data transfer objects.
 */
@Mapper(componentModel = "jakarta")
public interface UserMapper {

    /**
     * Converts a User entity to its corresponding DTO.
     *
     * @param entity the User entity
     * @return the mapped UserDto
     */
    UserDto toDto(User entity);

    /**
     * Converts a User DTO to its corresponding entity.
     *
     * @param dto the UserDto
     * @return the mapped User entity
     */
    User toEntity(UserDto dto);

    /**
     * Converts a list of User entities to a list of DTOs.
     *
     * @param entitiyList list of User entities
     * @return list of UserDto objects
     */
    List<UserDto> toDtoList(List<User> entitiyList);

    /**
     * Converts a list of User DTOs to a list of entities.
     *
     * @param dtoList list of UserDto objects
     * @return list of User entities
     */
    List<User> toEntityList(List<UserDto> dtoList);
}
