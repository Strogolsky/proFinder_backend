package fit.biejk.dto;

import fit.biejk.entity.ProposalStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderProposalDto {

    private Long id;

    private Long orderId;

    private Long specialistId;

    @NotBlank
    private String description;

    @NotNull
    private int price;

    private ProposalStatus status;
}
