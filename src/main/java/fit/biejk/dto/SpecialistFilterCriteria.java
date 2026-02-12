package fit.biejk.dto;

import jakarta.ws.rs.QueryParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SpecialistFilterCriteria extends PageRequest {

    @QueryParam("query")
    private String query;

    @QueryParam("location")
    private String location;

    public boolean hasFilters() {
        return (query != null && !query.isBlank()) ||
                (location != null && !location.isBlank());
    }
}