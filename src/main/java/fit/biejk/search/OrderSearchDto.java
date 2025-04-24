package fit.biejk.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderSearchDto {
    Long id;

    String status;

    List<String> services;

    String location;
}
