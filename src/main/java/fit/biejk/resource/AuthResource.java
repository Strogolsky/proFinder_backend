package fit.biejk.resource;

import fit.biejk.dto.*;
import fit.biejk.service.AuthService;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * REST resource for handling authentication-related operations such as sign-up, sign-in,
 * password change, and password recovery.
 * <p>
 * Routes are grouped under the "/auth" path.
 * </p>
 */
@Path("/v1/auth")
@Slf4j
public class AuthResource {

    /**
     * Service for handling authentication logic such as user registration, login,
     * password reset, and password change.
     */
    @Inject
    private AuthService authService;

    /**
     * Registers a new user account.
     *
     * @param request the sign-up request containing email, password, and role
     * @return HTTP 200 response with a generated authentication token
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
     * Authenticates a user and returns a JWT token if credentials are valid.
     *
     * @param request the sign-in request containing email, password, and role
     * @return HTTP 200 response with a generated authentication token
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

    /**
     * Changes the password of the authenticated user.
     * <p>
     * Requires the old password, new password, and confirmation.
     * </p>
     *
     * @param request contains old, new, and confirm passwords
     * @return HTTP 200 response with a new authentication token
     */
    @PUT
    @Path("/password/change")
    @Authenticated
    public Response changePassword(@Valid final ChangePasswordRequest request) {
        AuthResponse response = new AuthResponse(authService.changePassword(request));
        return Response.ok(response).build();
    }

    /**
     * Initiates password reset by generating and emailing a verification code to the user.
     *
     * @param request contains the email address of the user
     * @return HTTP 200 response if the operation succeeds (even if user does not exist)
     */
    @PUT
    @Path("/password/forgot")
    @PermitAll
    public Response forgotPassword(@Valid final ForgotPasswordRequest request) {
        authService.forgotPassword(request.getEmail());
        return Response.ok().build();
    }

    /**
     * Resets the user's password using a verification code previously sent by email.
     *
     * @param request contains email, verification code, and new password with confirmation
     * @return HTTP 200 response with a new authentication token
     */
    @PUT
    @Path("/password/reset")
    @PermitAll
    public Response resetPassword(@Valid final ResetPasswordRequest request) {
        AuthResponse response = new AuthResponse(authService.resetPassword(request));
        return Response.ok(response).build();
    }

    /**
     * Endpoint for changing the authenticated user's email address.
     * <p>
     * Requires the user to provide their new email and current password.
     * On success, returns a new authentication token.
     *
     * @param request the request containing the new email and current password
     * @return the HTTP response with the new authentication token
     */

    @PUT
    @Path("/email/change")
    @Authenticated
    public Response changeEmail(@Valid final ChangeEmailRequest request) {
        String token = authService.changeEmail(request.getNewEmail(), request.getPassword());
        AuthResponse response = new AuthResponse(token);
        return Response.ok(response).build();
    }

}
