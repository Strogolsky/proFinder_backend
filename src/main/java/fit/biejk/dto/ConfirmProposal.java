package fit.biejk.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object representing the confirmation of a proposal.
 * <p>
 * Contains the final agreed deadline and price for a given proposal.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmProposal {
    /** ID of the proposal to confirm. */
    @NotNull
    private Long proposalId;
    /**
     * The final agreed deadline for the proposal.
     * Must not be null.
     */
    @NotNull(message = "Final deadline cannot be null")
    private LocalDateTime finalDeadline;

    /**
     * The final agreed price for the proposal.
     * Must be a non-negative integer.
     */
    @Min(value = 0, message = "Final price must be non-negative")
    private int finalPrice;
}
