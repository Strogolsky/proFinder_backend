package fit.biejk.entity;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonbTransient
    private List<OrderProposal> orderProposals;

    @Column(name = "description")
    private String description;
    @Column(name = "price")
    private int price;
    @Column(name = "createAt", nullable = false, updatable = false)
    private final LocalDateTime createdAt = LocalDateTime.now();
    @Column(name = "deadline")
    private LocalDateTime deadline;
}
