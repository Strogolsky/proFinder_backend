package fit.biejk.resource;

import fit.biejk.dto.ChatDto;
import fit.biejk.entity.Chat;
import fit.biejk.entity.ChatMessage;
import fit.biejk.mapper.ChatMessageMapper;
import fit.biejk.service.AuthService;
import fit.biejk.service.ChatService;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/chat")
public class ChatResource {

    @Inject
    private ChatService chatService;

    @Inject
    private AuthService authService;

    @Inject
    private ChatMessageMapper chatMessageMapper;

    @POST
    @Authenticated
    public Response create(ChatDto chatDto) {
        Long userId = authService.getCurrentUserId();
        Chat chat = chatService.create(userId, chatDto.getRecipientId());
        return Response.ok().entity(chat).build();
    }

    @GET
    @Path("/{chatId}/history")
    @Authenticated
    public Response getHistory(Long chatId) {
        List<ChatMessage> result = chatService.getHistory(chatId);
        return Response.ok().entity(chatMessageMapper.toDtoList(result)).build();
    }

}
