package fit.biejk.mapper;

import fit.biejk.dto.ChatOutputMessage;
import fit.biejk.entity.ChatMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper interface for converting {@link ChatMessage} entities
 * to {@link ChatOutputMessage} DTOs.
 * <p>
 * Uses MapStruct to generate the implementation automatically.
 * </p>
 */
@Mapper(componentModel = "jakarta")
public interface ChatMessageMapper {

    /**
     * Converts a single {@link ChatMessage} entity to a {@link ChatOutputMessage} DTO.
     *
     * @param entity the {@link ChatMessage} entity to convert
     * @return the resulting {@link ChatOutputMessage} DTO
     */
    @Mapping(target = "senderId", source = "sender.id")
    @Mapping(target = "chatId", source = "chat.id")
    ChatOutputMessage toDto(ChatMessage entity);

    /**
     * Converts a list of {@link ChatMessage} entities to a list of {@link ChatOutputMessage} DTOs.
     *
     * @param entity list of chat messages to convert
     * @return list of mapped DTOs
     */
    List<ChatOutputMessage> toDtoList(List<ChatMessage> entity);
}
