package fit.biejk.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpecialistSearchDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String description;
    private Double averageRating;
    private List<String> services;
    private String location;
}

