package fit.biejk.dto;

import fit.biejk.entity.Location;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDto {
    private Long id;

    @Email
    private String email;

    @Length(max = 50)
    private String firstName;

    @Length(max = 50)
    private String lastName;

    @Length(max = 20)
    private String phoneNumber;

    private Location location;

    private LocalDateTime createAt;
}
