package fit.biejk.mapper;

import fit.biejk.dto.OrderDto;
import fit.biejk.dto.SpecialistDto;
import fit.biejk.entity.Order;
import fit.biejk.entity.Specialist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "jakarta")
public interface OrderMapper {

    @Mapping(target = "client", ignore = true)
    @Mapping(target = "orderProposals", ignore = true)
    Order toEntity(OrderDto orderDto);

    @Mapping(target = "clientId", source = "client.id")
    OrderDto toDto(Order order);


    List<OrderDto> toDtoList(List<Order> entitiyList);
    List<Order> toEntityList(List<OrderDto> dtoList);
}


