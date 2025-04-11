package fit.biejk.repository;

import fit.biejk.entity.Chat;
import fit.biejk.entity.ChatMessage;
import fit.biejk.entity.User;
import fit.biejk.service.ChatService;
import fit.biejk.service.UserService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ChatMessageService {
    @Inject
    private ChatMessageRepository chatMessageRepository;

    @Inject
    private UserService userService;

    @Inject
    private ChatService chatService;

    @Transactional
    public ChatMessage create(Long chatId, Long fromId, String content) {
        User fromUser = userService.getById(fromId);
        Chat chat = chatService.getById(chatId);

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(content);
        chatMessage.setSender(fromUser);
        chatMessage.setChat(chat);

        chatMessageRepository.persist(chatMessage);

        return chatMessage;
    }
}
