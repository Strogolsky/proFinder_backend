package fit.biejk.minIO;

import fit.biejk.service.AuthService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/users")
public class UserFileResource {

    @Inject
    UserFileService userFileService;
    @Inject
    AuthService authService;

    @POST
    @Path("/avatar")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadAvatar(FileUploadForm form) {
        try {
            Long userId = authService.getCurrentUserId();
            userFileService.uploadAvatar(userId, form.file, form.contentType);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}

