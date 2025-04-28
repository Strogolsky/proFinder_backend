package fit.biejk.mapper;

import fit.biejk.dto.UserDto;
import fit.biejk.entity.User;
import fit.biejk.minIO.UserFileService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class UserMapperImpl implements UserMapper {

    @Inject
    UserFileService userFileService;

    @Override
    public UserDto toDto(User entity) {
        if (entity == null) return null;

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

            }
        }

        return dto;
    }

    @Override
    public User toEntity(UserDto dto) {
        if (dto == null) return null;

        User entity = new User();
        entity.setId(dto.getId());
        entity.setEmail(dto.getEmail());
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setPhoneNumber(dto.getPhoneNumber());
        entity.setLocation(dto.getLocation());
        return entity;
    }

    @Override
    public List<UserDto> toDtoList(List<User> entitiyList) {
        if (entitiyList == null) return null;

        List<UserDto> dtoList = new ArrayList<UserDto>();
        for(User entity : entitiyList) {
            dtoList.add(toDto(entity));
        }
        return dtoList;
    }

    @Override
    public List<User> toEntityList(List<UserDto> dtoList) {
        if (dtoList == null) return null;

        List<User> entityList = new ArrayList<User>();
        for(UserDto dto : dtoList) {
            entityList.add(toEntity(dto));
        }
        return entityList;
    }

}
