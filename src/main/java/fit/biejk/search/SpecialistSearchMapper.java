package fit.biejk.search;

import fit.biejk.entity.ServiceOffering;
import fit.biejk.entity.Specialist;
import fit.biejk.service.SpecialistService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper for converting between {@link Specialist} entities and {@link SpecialistSearchDto} objects.
 * <p>
 * Used for transforming data between the database and the Elasticsearch index.
 * </p>
 */
@ApplicationScoped
public class SpecialistSearchMapper {

    /**
     * Service for retrieving specialist entities from the database.
     * <p>
     * Used for reverse-mapping from DTOs back to entities by ID.
     * </p>
     */
    @Inject
    private SpecialistService specialistService;

    /**
     * Converts a {@link Specialist} entity to a {@link SpecialistSearchDto}.
     *
     * @param specialist the specialist entity
     * @return the corresponding DTO for search indexing
     */
    public SpecialistSearchDto toDto(final Specialist specialist) {
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

    /**
     * Converts a {@link SpecialistSearchDto} back to a {@link Specialist} entity
     * using the database service.
     *
     * @param specialistDto the search DTO
     * @return the corresponding entity from the database
     */
    public Specialist toEntity(final SpecialistSearchDto specialistDto) {
        return specialistService.getById(specialistDto.getId());
    }

    /**
     * Converts a list of {@link SpecialistSearchDto} to a list of {@link Specialist} entities.
     *
     * @param specialistDtoList list of search DTOs
     * @return list of entities
     */
    public List<Specialist> toEntityList(final List<SpecialistSearchDto> specialistDtoList) {
        List<Specialist> specialists = new ArrayList<>();
        for (SpecialistSearchDto dto : specialistDtoList) {
            specialists.add(toEntity(dto));
        }
        return specialists;
    }
}
