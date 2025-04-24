package fit.biejk.search;

import fit.biejk.entity.Order;
import fit.biejk.entity.ServiceOffering;
import fit.biejk.service.OrderService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@ApplicationScoped
public class OrderSearchMapper {

    @Inject
    private OrderService orderService;

    public OrderSearchDto toDto(final Order entity) {
        List<String> serviceNames = entity.getServiceOfferings() == null
                ? List.of()
                : entity.getServiceOfferings().stream()
                .map(ServiceOffering::getName)
                .toList();

        String locationName = entity.getLocation() != null ? entity.getLocation().getName() : null;

        return new OrderSearchDto(
                entity.getId(),
                entity.getStatus().name(),
                serviceNames,
                locationName
        );
    }


    public Order toEntity(final OrderSearchDto dto) {
        return orderService.getById(dto.getId());
    }

    public List<Order> toEntityList(final List<OrderSearchDto> dtos) {
        List<Order> entities = new ArrayList<>();
        for (OrderSearchDto dto : dtos) {
            entities.add(toEntity(dto));
        }
        return entities;
    }

    public List<OrderSearchDto> toDtoList(final List<Order> entities) {
        List<OrderSearchDto> dtos = new ArrayList<>();
        for (Order entity : entities) {
            dtos.add(toDto(entity));
        }
        return dtos;
    }
}
