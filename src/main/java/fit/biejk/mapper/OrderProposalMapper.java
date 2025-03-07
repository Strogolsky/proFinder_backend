package fit.biejk.mapper;

import fit.biejk.dto.OrderProposalDto;
import fit.biejk.entity.OrderProposal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "jakarta")
public interface OrderProposalMapper {
    @Mapping(target = "order.id", ignore = true)
    @Mapping(target = "specialist.id", ignore = true)
    OrderProposal toEntity(OrderProposalDto orderProposalDto);

    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "specialistId", source = "specialist.id")
    OrderProposalDto toDto(OrderProposal orderProposal);


    List<OrderProposal> toEntityList(List<OrderProposalDto> orderProposalDtoList);
    List<OrderProposalDto> toDtoList(List<OrderProposal> orderProposalList);

}
