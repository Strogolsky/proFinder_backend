package fit.biejk.dto;

import fit.biejk.entity.Location;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

/**
 * Data Transfer Object representing a user.
 * <p>
 * Serves as the base user data structure for clients and specialists.
 * Includes common user attributes such as personal details, location,
 * contact information, and avatar image reference.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    /**
     * Maximum length for firstName and lastName fields.
     */
    public static final int NAME_MAX_LENGTH = 50;

    /**
     * Maximum length for phoneNumber field.
     */
    public static final int PHONE_MAX_LENGTH = 20;

    /**
     * Unique identifier of the user.
     */
    private Long id;

    /**
     * User's email address.
     * <p>
     * Temporary field, should be removed or hidden when full security is in place.
     * </p>
     */
    @Email
    private String email; // todo delete, when security added

    /**
     * First name of the user.
     */
    @Length(max = NAME_MAX_LENGTH)
    private String firstName;

    /**
     * Last name of the user.
     */
    @Length(max = NAME_MAX_LENGTH)
    private String lastName;

    /**
     * User's password.
     * <p>
     * Used only during authentication or registration; must not be returned in responses.
     * </p>
     */
    private String password; // todo delete, when security added

    /**
     * Phone number of the user.
     */
    @Length(max = PHONE_MAX_LENGTH)
    private String phoneNumber;

    /**
     * Geographical location associated with the user.
     */
    private Location location;

    /**
     * Timestamp indicating when the user account was created.
     */
    private LocalDateTime createAt;

    /**
     * Publicly accessible URL to the user's avatar image in object storage.
     */
    private String avatarUrl;
}
