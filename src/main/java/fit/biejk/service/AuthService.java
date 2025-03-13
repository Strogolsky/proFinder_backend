package fit.biejk.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import fit.biejk.entity.Client;
import fit.biejk.entity.Specialist;
import fit.biejk.entity.User;
import fit.biejk.entity.UserRole;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Duration;
import java.util.List;

@ApplicationScoped
public class AuthService {
    @Inject
    SpecialistService specialistService;

    @Inject
    ClientService clientService;

    @Inject
    UserService userService;

    @Inject
    SecurityIdentity securityIdentity;

    public String signUp(String email, String password, UserRole role) {
        String jwtToken = "";
        if (role == UserRole.SPECIALIST) {
            Specialist specialist = new Specialist();
            specialist.setEmail(email);
            specialist.setPassword(hashPassword(password));
            specialist.setRole(role);

            Specialist newSpecialist = specialistService.create(specialist);

            jwtToken = generateJWT(newSpecialist, role);

        } else if (role == UserRole.CLIENT) {
            Client client = new Client();

            client.setEmail(email);
            client.setPassword(hashPassword(password));
            client.setRole(role);

            Client newClient = clientService.create(client);

            jwtToken = generateJWT(newClient, role);
        }
        return jwtToken;
    }

    public String signIn(String email, String password, UserRole role) {
        User user = userService.getByEmail(email);
        verifyPassword(password, user.getPassword());
        if(user.getRole() != role) {
            throw new IllegalArgumentException("Invalid role");
        }
        return generateJWT(user, role);
    }

    private String generateJWT(User user, UserRole role) {
        return Jwt.issuer("quarkus-app")
                .subject(user.getId().toString())
                .claim("groups", List.of(user.getRole().name()))
                .expiresIn(Duration.ofHours(24))
                .sign();
    }

    private boolean verifyPassword(String password, String hashed) {
        return BCrypt.verifyer().verify(password.toCharArray(), hashed).verified;
    }

    public Long getCurrentUserId() {
        System.out.println(securityIdentity.getPrincipal().getName());
        return Long.parseLong(securityIdentity.getPrincipal().getName());
    }

    private String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }
}
