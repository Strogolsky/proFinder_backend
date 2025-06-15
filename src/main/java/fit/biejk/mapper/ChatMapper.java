package fit.biejk.mapper;

import fit.biejk.dto.ChatResponse;
import fit.biejk.entity.Chat;
import fit.biejk.entity.User;
import org.mapstruct.Context;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "jakarta")
public interface ChatMapper {

    @Mapping(target = "chatId",          source = "id")
    @Mapping(target = "partnerId",
            expression = "java(getPartner(chat, currentUserId).getId())")
    @Mapping(target = "partnerFirstName",
            expression = "java(getPartner(chat, currentUserId).getFirstName())")
    ChatResponse toDto(Chat chat, @Context Long currentUserId);

    @IterableMapping(elementTargetType = ChatResponse.class)
    List<ChatResponse> toDtoList(List<Chat> chats, @Context Long currentUserId);

    default User getPartner(Chat chat, Long currentUserId) {
        return chat.getUser1().getId().equals(currentUserId)
                ? chat.getUser2()
                : chat.getUser1();
    }
}
