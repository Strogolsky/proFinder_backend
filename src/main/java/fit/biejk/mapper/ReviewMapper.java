package fit.biejk.mapper;

import fit.biejk.dto.ReviewDto;
import fit.biejk.entity.Review;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "jakarta", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReviewMapper {

    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "specialist.id", target = "specialistId")
    @Mapping(source = "order.id", target = "orderId")
    ReviewDto toDto(Review entity);

    @Mapping(target = "client", ignore = true)
    @Mapping(target = "specialist", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "createAt", ignore = true)
    Review toEntity(ReviewDto dto);

    List<ReviewDto> toDtoList(List<Review> entityList);

    List<Review> toEntityList(List<ReviewDto> dtoList);
}
