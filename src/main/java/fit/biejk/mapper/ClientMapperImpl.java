package fit.biejk.mapper;

import fit.biejk.dto.ClientDto;
import fit.biejk.entity.Client;
import fit.biejk.minIO.UserFileService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link ClientMapper}.
 * <p>
 * Handles conversion between {@link Client} entities and {@link ClientDto} DTOs,
 * including generation of avatar URLs from object storage.
 * </p>
 */
@ApplicationScoped
@Slf4j
public class ClientMapperImpl implements ClientMapper {

    /**
     * Service for generating avatar download URLs from MinIO.
     */
    @Inject
    private UserFileService userFileService;

    /**
     * Converts a {@link Client} entity to a {@link ClientDto}.
     *
     * @param entity the Client entity
     * @return the corresponding ClientDto
     */
    @Override
    public ClientDto toDto(final Client entity) {
        if (entity == null) {
            return null;
        }

        log.debug("Mapping Client entity to DTO: id={}", entity.getId());

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

    /**
     * Converts a {@link ClientDto} to a {@link Client} entity.
     *
     * @param dto the ClientDto
     * @return the corresponding Client entity
     */
    @Override
    public Client toEntity(final ClientDto dto) {
        if (dto == null) {
            return null;
        }

        log.debug("Mapping ClientDto to entity: id={}", dto.getId());

        Client entity = new Client();
        entity.setId(dto.getId());
        entity.setEmail(dto.getEmail());
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setPhoneNumber(dto.getPhoneNumber());
        entity.setLocation(dto.getLocation());

        return entity;
    }

    /**
     * Converts a list of {@link Client} entities to a list of {@link ClientDto} DTOs.
     *
     * @param entityList the list of Client entities
     * @return the list of ClientDto objects
     */
    @Override
    public List<ClientDto> toDtoList(final List<Client> entityList) {
        if (entityList == null) {
            return null;
        }

        log.debug("Mapping list of Client entities to DTOs: size={}", entityList.size());

        List<ClientDto> dtoList = new ArrayList<>(entityList.size());
        for (Client client : entityList) {
            dtoList.add(toDto(client));
        }
        return dtoList;
    }

    /**
     * Converts a list of {@link ClientDto} DTOs to a list of {@link Client} entities.
     *
     * @param dtoList the list of ClientDto objects
     * @return the list of Client entities
     */
    @Override
    public List<Client> toEntityList(final List<ClientDto> dtoList) {
        if (dtoList == null) {
            return null;
        }

        log.debug("Mapping list of Client DTOs to entities: size={}", dtoList.size());

        List<Client> entityList = new ArrayList<>(dtoList.size());
        for (ClientDto dto : dtoList) {
            entityList.add(toEntity(dto));
        }
        return entityList;
    }
}
