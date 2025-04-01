package fit.biejk.entity;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.EnumType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Entity representing a specialist.
 * <p>
 * Extends {@link User} and includes details specific to specialists such as specialization, description, proposals,
 * and schedule.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "specialist")
public class Specialist extends User {

    /**
     * Specialist's professional specialization.
     */
    @Column(name = "specialization")
    @Enumerated(EnumType.STRING)
    private Specialization specialization;


    @Column(name = "average_rating")
    private Double averageRating;

    /**
     * Detailed description of the specialist's services or skills.
     */
    @Column(name = "description")
    private String description;

    /**
     * List of order proposals submitted by the specialist.
     * Ignored during JSON serialization.
     */
    @OneToMany(mappedBy = "specialist", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonbTransient
    private List<OrderProposal> orderProposals;

    /**
     * Schedule containing available time slots for the specialist.
     */
    @OneToMany(mappedBy = "specialist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TimeSlot> schedule;

    @OneToMany(mappedBy = "specialist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;

}
