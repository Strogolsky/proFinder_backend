package fit.biejk.dto;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Data;

@Data
public class PageRequest {

    @QueryParam("page")
    @DefaultValue("1")
    @Min(value = 1, message = "Page must be >= 1")
    private int page;

    @QueryParam("size")
    @DefaultValue("10")
    @Max(value = 100, message = "Size must be <= 100")
    private int size;

}
