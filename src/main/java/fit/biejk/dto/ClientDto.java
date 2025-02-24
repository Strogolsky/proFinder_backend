package fit.biejk.dto;

import fit.biejk.entity.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDto {
    private Long id;

    private String email; // todo delete, when security added

    private String firstName;

    private String lastName;

    private String password; // todo delete, when security added

    private String phoneNumber;

    private Location location;

    private LocalDateTime createAt;
}
