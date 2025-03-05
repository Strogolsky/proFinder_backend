package fit.biejk.mapper;

import fit.biejk.dto.OrderDto;
import fit.biejk.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "jakarta")
public interface OrderMapper {

    @Mapping(target = "client", ignore = true)
    @Mapping(target = "specialist", ignore = true)
    Order toEntity(OrderDto orderDto);

    @Mapping(target = "clientId", source = "client.id")
    @Mapping(target = "specialistId", source = "specialist.id")
    OrderDto toDto(Order order);
}


