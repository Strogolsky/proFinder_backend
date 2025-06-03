package fit.biejk.dto;

import java.io.InputStream;

public record AvatarData(InputStream stream, String contentType) {
}
