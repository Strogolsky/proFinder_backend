package fit.biejk.entity;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Entity representing a client.
 * <p>
 * Inherits from {@link User} and defines relationships to orders and reviews created by the client.
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "client")
public class Client extends User {

    /**
     * List of orders created by the client.
     * <p>
     * This field is ignored during JSON serialization to prevent recursion.
     * </p>
     */
    @OneToMany(mappedBy = "client")
    @JsonbTransient
    private List<Order> orders;

    /**
     * List of reviews submitted by the client.
     */
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;
}
