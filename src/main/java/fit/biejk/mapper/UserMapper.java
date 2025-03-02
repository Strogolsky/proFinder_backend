package fit.biejk.mapper;

import fit.biejk.dto.UserDto;
import fit.biejk.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "jakarta")
public interface UserMapper {
    UserDto toDto(User entity);
    User toEntity(UserDto dto);

    List<UserDto> toDtoList(List<User> entitiyList);
    List<User> toEntityList(List<UserDto> dtoList);

}
