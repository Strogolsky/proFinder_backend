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

@Path("/auth")
@Slf4j
public class AuthResource {

    @Inject
    AuthService authService;

    @POST
    @Path("/signUp")
    @PermitAll
    public Response signUp(@Valid AuthRequest request) {
        log.info("Sign up request: {}", request);
        String result = authService.signUp(request.getEmail(), request.getPassword(), request.getRole());
        log.debug("Sign up result: {}", result);
        AuthResponse response = new AuthResponse(result);
        log.info("Sign up response: {}", response);
        return Response.ok(response).build();
    }

    @POST
    @Path("/signIn")
    @PermitAll
    public Response signIn(@Valid AuthRequest request) {
        log.info("Sign in request: {}", request);
        String result = authService.signIn(request.getEmail(), request.getPassword(), request.getRole());
        log.debug("Sign in result: {}", result);
        AuthResponse response = new AuthResponse(result);
        log.info("Sign in response: {}", response);
        return Response.ok(response).build();
    }
}
