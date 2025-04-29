package fit.biejk.minIO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jboss.resteasy.reactive.RestForm;

import java.io.InputStream;

/**
 * Represents a multipart form used for uploading a file.
 * <p>
 * This class is used in REST endpoints to receive file data
 * and its associated content type from a client.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadForm {

    /**
     * The binary stream of the uploaded file.
     * <p>
     * This field is automatically populated by RESTEasy when a file is submitted
     * as part of a multipart/form-data request.
     * </p>
     */
    @RestForm
    private InputStream file;

    /**
     * The MIME type of the uploaded file (e.g., image/jpeg, application/pdf).
     */
    @RestForm
    private String contentType;
}
