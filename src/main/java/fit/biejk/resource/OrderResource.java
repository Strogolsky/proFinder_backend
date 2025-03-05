package fit.biejk.resource;

import fit.biejk.dto.OrderDto;
import fit.biejk.entity.Client;
import fit.biejk.entity.Order;
import fit.biejk.mapper.OrderMapper;
import fit.biejk.service.ClientService;
import fit.biejk.service.OrderService;
import fit.biejk.service.SpecialistService;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/order")
public class OrderResource {
    @Inject
    OrderService orderService;
    @Inject
    OrderMapper orderMapper;
    @Inject
    ClientService clientService;
    @Inject
    SpecialistService specialistService;

    @POST
    public Response create(OrderDto dto) {
        Order order = orderMapper.toEntity(dto);

        Client client = clientService.getById(dto.getClientId());
        order.setClient(client);
//        if (dto.getSpecialistId() != null) {
//            Specialist specialist = specialistService.getById(dto.getSpecialistId());
//            order.setSpecialist(specialist);
//        }
        Order result = orderService.create(order);
        return Response.ok(orderMapper.toDto(result)).build();
    }

    @PUT
    @Path("/{orderId}/cancel")
    public Response cancel(Long orderId) {
        Order result = orderService.cancel(orderId);
        return Response.ok(orderMapper.toDto(result)).build();
    }
}
