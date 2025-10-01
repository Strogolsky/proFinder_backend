package fit.biejk.service;

import fit.biejk.entity.User;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Duration;
import java.util.List;

@ApplicationScoped
public class TokenService {
    private static final Duration DEFAULT_EXPIRATION = Duration.ofHours(24);

    private String generateJWT(final User user, final Duration expiration) {
        return Jwt.issuer("quarkus-app")
                .subject(user.getId().toString())
                .claim("groups", List.of(user.getRole().name()))
                .expiresIn(expiration)
                .sign();
    }

    public String generateToken(final User user) {
        return generateJWT(user, DEFAULT_EXPIRATION);
    }
}
