package fit.biejk.dto;

import fit.biejk.entity.Location;
import jakarta.validation.constraints.Email;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

public class UserDto {
    private Long id;

    @Email
    private String email; // todo delete, when security added

    @Length(max = 50)
    private String firstName;

    @Length(max = 50)
    private String lastName;

    private String password; // todo delete, when security added

    @Length(max = 20)
    private String phoneNumber;

    private Location location;

    private LocalDateTime createAt;
}
