package fit.biejk.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;
import lombok.Data;

/**
 * Basic DTO for handling pagination parameters in API requests.
 */
@Data
public class PageRequest {

    /**
     * The page number to retrieve, starting from 1.
     */
    @QueryParam("page")
    @DefaultValue("1")
    @Min(value = 1, message = "Page must be >= 1")
    private int page;

    /**
     * The number of items per page. Maximum allowed value is 100.
     */
    @QueryParam("size")
    @DefaultValue("10")
    @Max(value = 100, message = "Size must be <= 100")
    private int size;

}
