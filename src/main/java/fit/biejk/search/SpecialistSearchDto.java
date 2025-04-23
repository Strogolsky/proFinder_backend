package fit.biejk.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data Transfer Object representing a specialist in the search index.
 * <p>
 * Used for indexing and retrieving specialist data from Elasticsearch.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpecialistSearchDto {

    /**
     * Unique identifier of the specialist.
     */
    private Long id;

    /**
     * First name of the specialist.
     */
    private String firstName;

    /**
     * Last name of the specialist.
     */
    private String lastName;

    /**
     * Short description or bio of the specialist.
     */
    private String description;

    /**
     * Average rating of the specialist (calculated from reviews).
     */
    private Double averageRating;

    /**
     * List of services provided by the specialist.
     */
    private List<String> services;

    /**
     * Location (city or area) where the specialist operates.
     */
    private String location;
}
