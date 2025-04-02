package fit.biejk.entity;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity representing an order placed by a client.
 * <p>
 * Contains details about specialization, status, price, deadline,
 * proposals from specialists, and a linked review after completion.
 * </p>
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
     * <p>
     * Ignored during JSON serialization to avoid circular references.
     * </p>
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonbTransient
    private List<OrderProposal> orderProposals;

    /**
     * Description provided by the client for the order.
     */
    @Column(name = "description")
    private String description;

    /**
     * Price set for the order after confirmation.
     */
    @Column(name = "price")
    private int price;

    /**
     * Review associated with the order after it has been completed.
     */
    @OneToOne
    private Review review;

    /**
     * Timestamp indicating when the order was created.
     * <p>
     * This field is final and set at creation time.
     * </p>
     */
    @Column(name = "createAt", nullable = false, updatable = false)
    private final LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Deadline for completing the order.
     */
    @Column(name = "deadline")
    private LocalDateTime deadline;
}
