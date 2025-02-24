package fit.biejk.mapper;

import fit.biejk.dto.SpecialistDto;
import fit.biejk.entity.Specialist;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "jakarta")
public interface SpecialistMapper{
    SpecialistDto toDto(Specialist entity);
    Specialist toEntity(SpecialistDto dto);

    List<SpecialistDto> toDtoList(List<Specialist> entitiyList);
    List<Specialist> toEntityList(List<SpecialistDto> dtoList);

}
