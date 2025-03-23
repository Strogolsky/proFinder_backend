package fit.biejk.entity;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing a specialist's available time slot for appointments.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "time_slot")
public class TimeSlot {

    /**
     * Unique identifier for the time slot.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Start time of the available time slot.
     */
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    /**
     * End time of the available time slot.
     */
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    /**
     * Indicates if the time slot is currently available.
     */
    @Column(name = "available")
    private boolean available;

    /**
     * Specialist associated with this time slot.
     * Ignored during JSON serialization.
     */
    @ManyToOne
    @JoinColumn(name = "specialist_id", nullable = false)
    @JsonbTransient
    private Specialist specialist;
}
