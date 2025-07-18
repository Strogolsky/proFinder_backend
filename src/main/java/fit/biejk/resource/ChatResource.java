package fit.biejk.resource;

import fit.biejk.dto.CreateChatRequest;
import fit.biejk.entity.Chat;
import fit.biejk.entity.ChatMessage;
import fit.biejk.mapper.ChatMapper;
import fit.biejk.mapper.ChatMessageMapper;
import fit.biejk.service.AuthService;
import fit.biejk.service.ChatService;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import java.util.List;

/**
 * REST resource that handles operations related to chat creation and message history retrieval.
 */
@Path("/chat")
public class ChatResource {

    /**
     * Service for managing chat operations.
     */
    @Inject
    private ChatService chatService;

    /**
     * Service for retrieving the current authenticated user's information.
     */
    @Inject
    private AuthService authService;

    /**
     * Mapper to convert ChatMessage entities to DTOs.
     */
    @Inject
    private ChatMessageMapper chatMessageMapper;

    /** Mapper to convert chat entities to DTOs. */
    @Inject
    private ChatMapper chatMapper;

    /**
     * Creates a new chat between the authenticated user and the recipient.
     *
     * @param request The DTO containing the recipient's user ID.
     * @return A Response containing the created Chat object.
     */
    @POST
    @Authenticated
    public Response create(@Valid final CreateChatRequest request) {
        Long userId = authService.getCurrentUserId();
        Chat chat = chatService.create(userId, request.getRecipientId());
        return Response.ok().entity(chatMapper.toDto(chat, userId)).build();
    }

    /**
     * Retrieves the message history for a specific chat.
     *
     * @param chatId The ID of the chat whose history is to be retrieved.
     * @return A Response containing a list of ChatOutputMessage DTOs.
     */
    @GET
    @Path("/{chatId}/history")
    @Authenticated
    public Response getHistory(final Long chatId) {
        List<ChatMessage> result = chatService.getHistory(chatId);
        return Response.ok().entity(chatMessageMapper.toDtoList(result)).build();
    }

    /**
     * Returns a list of chats associated with the current user.
     *
     * @return list of chat DTOs
     */
    @GET
    @Path("/profile")
    @Authenticated
    public Response getChatsByProfile() {
        Long userId = authService.getCurrentUserId();
        List<Chat> result = chatService.getByUserId(userId);
        return Response.ok().entity(chatMapper.toDtoList(result, userId)).build();
    }

}
