package fit.biejk.dto;

import fit.biejk.entity.Location;
import fit.biejk.entity.Specialization;
import fit.biejk.entity.TimeSlot;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpecialistDto {
    private Long id;

    @Email
    private String email;

    @Length(max = 50)
    private String firstName;

    @Length(max = 50)
    private String lastName;

    private String password; // todo delete, when create response

    @Length(max = 20)
    private String phoneNumber;

    private Location location;

    private LocalDateTime createAt;

    private Specialization specialization;

    private String description;

    private List<TimeSlot> schedule;
}
