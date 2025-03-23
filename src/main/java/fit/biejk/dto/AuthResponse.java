package fit.biejk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Data Transfer Object for authentication responses.
 * <p>
 * Contains the JWT token returned upon successful authentication.
 * </p>
 */
@Data
@AllArgsConstructor
public class AuthResponse {

    /**
     * JWT authentication token.
     */
    private String token;
}
