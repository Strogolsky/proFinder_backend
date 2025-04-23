package fit.biejk.search;

import fit.biejk.dto.SpecialistDto;
import fit.biejk.entity.ServiceOffering;
import fit.biejk.entity.Specialist;
import fit.biejk.service.SpecialistService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class SpecialistSearchMapper {

    @Inject
    SpecialistService specialistService;

    public SpecialistSearchDto toDto(Specialist specialist) {
        List<String> serviceNames = specialist.getServiceOfferings() == null
                ? List.of()
                : specialist.getServiceOfferings().stream()
                .map(ServiceOffering::getName)
                .toList();

        return new SpecialistSearchDto(
                specialist.getId(),
                specialist.getFirstName(),
                specialist.getLastName(),
                specialist.getDescription(),
                specialist.getAverageRating(),
                serviceNames,
                specialist.getLocation().getName()
        );
    }

    public Specialist toEntity(SpecialistSearchDto specialistDto) {
        return specialistService.getById(specialistDto.getId());
    }

    public List<Specialist> toEntityList(List<SpecialistSearchDto> specialistDtoList) {
        List<Specialist> specialists = new ArrayList<>();
        for (SpecialistSearchDto dto : specialistDtoList) {
            specialists.add(toEntity(dto));
        }
        return specialists;
    }
}
