package fit.biejk.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Base entity representing a system user.
 * <p>
 * Extended by other entities such as {@link Client} and {@link Specialist}.
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public class User extends PanacheEntityBase {
    /**
     * Maximum length for firstName and lastName fields.
     */
    public static final int NAME_MAX_LENGTH = 50;

    /**
     * Maximum length for phoneNumber field.
     */
    public static final int PHONE_MAX_LENGTH = 20;

    /**
     * Unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User's email address. Must be unique.
     */
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    /**
     * User's password.
     */
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * User's first name.
     */
    @Column(name = "first_name", length = NAME_MAX_LENGTH)
    private String firstName;

    /**
     * User's last name.
     */
    @Column(name = "last_name", length = NAME_MAX_LENGTH)
    private String lastName;

    /**
     * User's phone number.
     */
    @Column(name = "phone_number", length = PHONE_MAX_LENGTH)
    private String phoneNumber;

    /**
     * User's location.
     */
    @Column(name = "location")
    @Enumerated(EnumType.STRING)
    private Location location;

    /**
     * Role assigned to the user.
     */
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private UserRole role;

    /**
     * Timestamp when the user was created.
     */
    @Column(name = "createAt", nullable = false, updatable = false)
    private final LocalDateTime createAt = LocalDateTime.now();

    @OneToMany(mappedBy = "user1", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonbTransient
    private List<Chat> initiatedChats;

    @OneToMany(mappedBy = "user2", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonbTransient
    private List<Chat> receivedChats;
}
