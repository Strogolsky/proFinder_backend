package fit.biejk.resource;

import fit.biejk.dto.AuthRequest;
import fit.biejk.dto.AuthResponse;
import fit.biejk.service.AuthService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * REST resource for handling authentication-related operations such as sign-up and sign-in.
 */
@Path("/auth")
@Slf4j
public class AuthResource {

    /**
     * Service for handling authentication logic such as user registration and login.
     * <p>
     * Injected by the Jakarta CDI container.
     * </p>
     */
    @Inject
    private AuthService authService;


    /**
     * Registers a new user account.
     *
     * @param request the sign-up request containing email, password, and role
     * @return HTTP response with a generated authentication token
     */
    @POST
    @Path("/signUp")
    @PermitAll
    public Response signUp(@Valid final AuthRequest request) {
        log.info("Sign up request: {}", request);
        String result = authService.signUp(request.getEmail(), request.getPassword(), request.getRole());
        log.debug("Sign up result: {}", result);
        AuthResponse response = new AuthResponse(result);
        log.info("Sign up response: {}", response);
        return Response.ok(response).build();
    }

    /**
     * Authenticates a user and returns a token.
     *
     * @param request the sign-in request containing email, password, and role
     * @return HTTP response with a generated authentication token
     */
    @POST
    @Path("/signIn")
    @PermitAll
    public Response signIn(@Valid final AuthRequest request) {
        log.info("Sign in request: {}", request);
        String result = authService.signIn(request.getEmail(), request.getPassword(), request.getRole());
        log.debug("Sign in result: {}", result);
        AuthResponse response = new AuthResponse(result);
        log.info("Sign in response: {}", response);
        return Response.ok(response).build();
    }
}
