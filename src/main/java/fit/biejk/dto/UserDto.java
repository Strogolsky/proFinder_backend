package fit.biejk.dto;

import fit.biejk.entity.Location;
import jakarta.validation.constraints.Email;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

/**
 * Data Transfer Object representing a user.
 * <p>
 * Contains basic user details including personal information and contact details.
 * </p>
 */
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
     * User's email address. (Temporary field, remove when security is implemented.)
     */
    @Email
    private String email; // todo delete, when security added

    /**
     * First name of the user, maximum length of 50 characters.
     */
    @Length(max = NAME_MAX_LENGTH)
    private String firstName;

    /**
     * Last name of the user, maximum length of 50 characters.
     */
    @Length(max = NAME_MAX_LENGTH)
    private String lastName;

    /**
     * User's password. (Temporary field, remove when security is implemented.)
     */
    private String password; // todo delete, when security added

    /**
     * Phone number of the user, maximum length of 20 characters.
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
}
