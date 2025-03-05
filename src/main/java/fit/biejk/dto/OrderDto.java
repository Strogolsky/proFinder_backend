package fit.biejk.dto;


import fit.biejk.entity.OrderStatus;
import fit.biejk.entity.Specialization;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@ApplicationScoped
public class OrderDto {
    private Long id;
    private Long clientId;
    private Specialization specialization;
    private OrderStatus status;
    private String description;
    private int price;
    private LocalDateTime createdAt;
    private LocalDateTime deadline;
}
