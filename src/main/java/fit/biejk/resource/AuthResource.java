package fit.biejk.resource;

import fit.biejk.dto.AuthRequest;
import fit.biejk.dto.AuthResponse;
import fit.biejk.service.AuthService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/auth")
public class AuthResource {

    @Inject
    AuthService authService;

    @POST
    @Path("/signUp")
    @PermitAll
    public Response signUp(@Valid AuthRequest request) {
        String result = authService.signUp(request.getEmail(), request.getPassword(), request.getRole());
        AuthResponse response = new AuthResponse(result);
        return Response.ok(response).build();
    }

    @POST
    @Path("/signIn")
    @PermitAll
    public Response signIn(@Valid AuthRequest request) {
        String result = authService.signIn(request.getEmail(), request.getPassword(), request.getRole());
        AuthResponse response = new AuthResponse(result);
        return Response.ok(response).build();
    }


}
