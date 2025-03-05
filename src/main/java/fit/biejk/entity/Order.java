package fit.biejk.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "specialization")
    @Enumerated(EnumType.STRING)
    private Specialization specialization;

    @ManyToOne
    @JoinColumn(name = "specialist_id")
    private Specialist specialist;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "description")
    private String description;
    @Column(name = "price")
    private int price;
    @Column(name = "createAt", nullable = false, updatable = false)
    private final LocalDateTime createdAt = LocalDateTime.now();
    @Column(name = "deadline")
    private LocalDateTime deadline;
}
