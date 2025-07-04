package fit.biejk.minIO;

import fit.biejk.dto.AvatarData;
import fit.biejk.service.AuthService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;


/**
 * REST endpoint for handling user file uploads such as avatar images.
 * <p>
 * Accepts multipart/form-data requests from authenticated users and delegates
 * the upload to {@link UserFileService}.
 * </p>
 */
@Path("/users")
@Slf4j
public class UserFileResource {

    /**
     * Service responsible for handling file uploads and interactions with MinIO.
     */
    @Inject
    private UserFileService userFileService;

    /**
     * Service for retrieving the currently authenticated user ID and auth logic.
     */
    @Inject
    private AuthService authService;

    /**
     * Handles avatar upload for the currently authenticated user.
     *
     * @param form the uploaded file and its metadata
     * @return HTTP 200 if upload was successful, 500 otherwise
     */
    @POST
    @Path("/avatar")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadAvatar(final FileUploadForm form) {
        Long userId = authService.getCurrentUserId();
        log.info("Received avatar upload request from userId={}", userId);

        try {
            userFileService.uploadAvatar(userId, form.getFile(), form.getContentType());
            log.debug("Avatar uploaded successfully for userId={}", userId);
            return Response.ok().build();
        } catch (Exception e) {
            log.error("Failed to upload avatar for userId={}: {}", userId, e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /**
     * Retrieves the avatar image of the specified user.
     * <p>
     * This endpoint is publicly accessible and returns the user's avatar image stream.
     * The response will have the appropriate content type (e.g., image/png, image/jpeg)
     * depending on the stored image format.
     * </p>
     *
     * @param userId the ID of the user whose avatar is to be retrieved
     * @return a {@link Response} containing the image stream with proper content type,
     *         or an error response with status {@code 500 Internal Server Error} if retrieval fails
     */
    @GET
    @Path("/{userId}/avatar")
    @Produces("image/*")
    @PermitAll
    public Response getAvatar(@PathParam("userId") final Long userId) {
        try {
            AvatarData data = userFileService.getAvatar(userId);

            return Response.ok(data.stream())
                    .type(data.contentType())
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }

    }
}
