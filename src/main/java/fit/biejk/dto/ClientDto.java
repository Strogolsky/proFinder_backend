package fit.biejk.dto;

import fit.biejk.entity.Location;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

/**
 * Data Transfer Object representing a client.
 * <p>
 * Contains details about the client, such as personal information,
 * contact details, and account creation timestamp.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDto {

    /**
     * Maximum length for firstName and lastName fields.
     */
    public static final int NAME_MAX_LENGTH = 50;

    /**
     * Maximum length for phoneNumber field.
     */
    public static final int PHONE_MAX_LENGTH = 20;

    /**
     * Unique identifier of the client.
     */
    private Long id;

    /**
     * Email address of the client, must be a valid email format.
     */
    @Email
    private String email;

    /**
     * First name of the client, maximum length of 50 characters.
     */
    @Length(max = NAME_MAX_LENGTH)
    private String firstName;

    /**
     * Last name of the client, maximum length of 50 characters.
     */
    @Length(max = NAME_MAX_LENGTH)
    private String lastName;

    /**
     * Client's password. (Temporary field, remove when creating response.)
     */
    private String password; // todo delete, when create response

    /**
     * Phone number of the client, maximum length of 20 characters.
     */
    @Length(max = PHONE_MAX_LENGTH)
    private String phoneNumber;

    /**
     * Geographical location associated with the client.
     */
    private Location location;

    /**
     * Timestamp indicating when the client account was created.
     */
    private LocalDateTime createAt;

    private String avatarUrl;
}
