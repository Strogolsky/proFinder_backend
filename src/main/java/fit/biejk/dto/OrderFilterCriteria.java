package fit.biejk.dto;

import jakarta.ws.rs.QueryParam;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrderFilterCriteria extends PageRequest {

    @QueryParam("services")
    private List<String> services;

    @QueryParam("location")
    private String location;

    @QueryParam("query")
    private String query;

    public boolean hasFilters() {
        return (services != null && !services.isEmpty()) ||
                (location != null && !location.isBlank()) ||
                (query != null && !query.isBlank());
    }
}