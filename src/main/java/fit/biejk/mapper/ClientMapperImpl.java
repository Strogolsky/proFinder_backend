package fit.biejk.mapper;

import fit.biejk.dto.ClientDto;
import fit.biejk.entity.Client;
import fit.biejk.minIO.UserFileService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@Slf4j
public class ClientMapperImpl implements ClientMapper {

    @Inject
    UserFileService userFileService;

    @Override
    public ClientDto toDto(Client entity) {
        if (entity == null) {
            return null;
        }

        ClientDto dto = new ClientDto();
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
                log.warn("Failed to generate avatar URL for clientId={}", entity.getId(), e);
            }
        }

        return dto;
    }

    @Override
    public Client toEntity(ClientDto dto) {
        if (dto == null) {
            return null;
        }

        Client entity = new Client();
        entity.setId(dto.getId());
        entity.setEmail(dto.getEmail());
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setPhoneNumber(dto.getPhoneNumber());
        entity.setLocation(dto.getLocation());

        // Password здесь не маппится, потому что в обычной ситуации DTO его не содержит
        return entity;
    }

    @Override
    public List<ClientDto> toDtoList(List<Client> entityList) {
        if (entityList == null) {
            return null;
        }

        List<ClientDto> dtoList = new ArrayList<>(entityList.size());
        for (Client client : entityList) {
            dtoList.add(toDto(client));
        }
        return dtoList;
    }

    @Override
    public List<Client> toEntityList(List<ClientDto> dtoList) {
        if (dtoList == null) {
            return null;
        }

        List<Client> entityList = new ArrayList<>(dtoList.size());
        for (ClientDto dto : dtoList) {
            entityList.add(toEntity(dto));
        }
        return entityList;
    }
}
