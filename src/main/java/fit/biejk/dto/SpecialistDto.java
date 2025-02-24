package fit.biejk.dto;

import fit.biejk.entity.Location;
import fit.biejk.entity.Specialization;
import fit.biejk.entity.TimeSlot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpecialistDto {
    private Long id;

    private String email; // todo delete, when security added

    private String firstName;

    private String lastName;

    private String password; // todo delete, when security added

    private String phoneNumber;

    private Location location;

    private LocalDateTime createAt;

    private Specialization specialization;

    private String description;

    private List<TimeSlot> schedule;
}
