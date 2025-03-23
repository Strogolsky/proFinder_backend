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

/**
 * Data Transfer Object representing a specialist.
 * <p>
 * Contains details about a specialist including contact info, location,
 * specialization, description, and availability schedule.
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpecialistDto {
    /**
     * Maximum length for firstName and lastName fields.
     */
    public static final int NAME_MAX_LENGTH = 50;

    /**
     * Maximum length for phoneNumber field.
     */
    public static final int PHONE_MAX_LENGTH = 20;

    /**
     * Unique identifier of the specialist.
     */
    private Long id;

    /**
     * Specialist's email address, must be a valid email format.
     */
    @Email
    private String email;

    /**
     * First name of the specialist, maximum length of 50 characters.
     */
    @Length(max = NAME_MAX_LENGTH)
    private String firstName;

    /**
     * Last name of the specialist, maximum length of 50 characters.
     */
    @Length(max = NAME_MAX_LENGTH)
    private String lastName;

    /**
     * Specialist's password. (Temporary field, remove when creating response.)
     */
    private String password; // todo delete, when create response

    /**
     * Phone number of the specialist, maximum length of 20 characters.
     */
    @Length(max = PHONE_MAX_LENGTH)
    private String phoneNumber;

    /**
     * Geographical location of the specialist.
     */
    private Location location;

    /**
     * Timestamp indicating when the specialist's account was created.
     */
    private LocalDateTime createAt;

    /**
     * Specialist's professional specialization.
     */
    private Specialization specialization;

    /**
     * Detailed description of the specialist's skills or services offered.
     */
    private String description;

    /**
     * List of available time slots for appointments.
     */
    private List<TimeSlot> schedule;
}
