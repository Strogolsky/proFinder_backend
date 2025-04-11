package fit.biejk.mapper;

import fit.biejk.dto.ChatOutputMessage;
import fit.biejk.entity.ChatMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "jakarta")
public interface ChatMessageMapper {
    @Mapping(target = "senderId", source = "sender.id")
    @Mapping(target = "chatId", source = "chat.id")
    ChatOutputMessage toDto(ChatMessage entity);
    List<ChatOutputMessage> toDtoList(List<ChatMessage> entity);
}
