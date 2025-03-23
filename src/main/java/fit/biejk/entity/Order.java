package fit.biejk.entity;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.GenerationType;
import jakarta.persistence.EnumType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity representing an order placed by a client.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    /**
     * Unique identifier for the order.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Client who placed the order.
     */
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    /**
     * Specialization required for fulfilling the order.
     */
    @Column(name = "specialization")
    @Enumerated(EnumType.STRING)
    private Specialization specialization;

    /**
     * Current status of the order.
     */
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    /**
     * List of proposals submitted for this order.
     * Ignored during JSON serialization.
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JsonbTransient
    private List<OrderProposal> orderProposals;

    /**
     * Description provided for the order.
     */
    @Column(name = "description")
    private String description;

    /**
     * Price specified for the order.
     */
    @Column(name = "price")
    private int price;

    /**
     * Timestamp indicating when the order was created.
     */
    @Column(name = "createAt", nullable = false, updatable = false)
    private final LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Deadline for completing the order.
     */
    @Column(name = "deadline")
    private LocalDateTime deadline;
}
