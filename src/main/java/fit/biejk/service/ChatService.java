package fit.biejk.service;

import fit.biejk.entity.Chat;
import fit.biejk.entity.ChatMessage;
import fit.biejk.entity.User;
import fit.biejk.repository.ChatRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@ApplicationScoped
public class ChatService {
    @Inject
    ChatRepository chatRepository;

    @Inject
    UserService userService;

    @Transactional
    public Chat create(Long userId1, Long userId2) {
        Long first = Math.min(userId1, userId2);
        Long second = Math.max(userId1, userId2);
        if(first == second) {
            throw new IllegalArgumentException("first and second are the same");
        }
        if (existByUsersId(first, second)) {
            throw new IllegalArgumentException("Chat already exists");
        }
        User user1 = userService.getById(first);
        User user2 = userService.getById(second);

        Chat chat = new Chat();
        chat.setUser1(user1);
        chat.setUser2(user2);

        chatRepository.persist(chat);
        return chat;
    }

    public Chat getById(Long chatId) {
        Chat result = chatRepository.findById(chatId);
        if(result == null) {
            throw new NotFoundException("Chat with id " + chatId + " not found");
        }
        return result;
    }

    public List<ChatMessage> getHistory(Long chatId) {
        Chat chat = getById(chatId);
        return chat.getMessages();
    }

    public boolean existById(Long chatId) {
        Chat chat = chatRepository.findById(chatId);
        return chat != null;
    }

    public boolean existByUsersId(Long userId1, Long userId2) {
        Chat chat  = chatRepository.findByUsersId(userId1, userId2);
        if (chat == null) {
            return false;
        }
        return true;
    }

}
