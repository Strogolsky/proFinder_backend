package fit.biejk.mapper;


import fit.biejk.dto.ClientDto;

import fit.biejk.entity.Client;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "jakarta")
public interface ClientMapper {
    ClientDto toDto(Client entity);
    Client toEntity(ClientDto dto);

    List<ClientDto> toDtoList(List<Client> entitiyList);
    List<Client> toEntityList(List<ClientDto> dtoList);
}
