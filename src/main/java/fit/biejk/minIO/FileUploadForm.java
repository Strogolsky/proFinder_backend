package fit.biejk.minIO;

import org.jboss.resteasy.reactive.RestForm;

import java.io.InputStream;

public class FileUploadForm {

    @RestForm
    public InputStream file;

    @RestForm
    public String contentType;
}
