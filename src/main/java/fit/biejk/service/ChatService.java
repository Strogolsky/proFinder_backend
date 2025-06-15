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
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Service for handling chat operations, such as creating new chats,
 * retrieving chat history, and checking chat existence.
 */
@NoArgsConstructor
@ApplicationScoped
@Slf4j
public class ChatService {

    /**
     * Repository for performing CRUD operations on {@link Chat} entities.
     */
    @Inject
    private ChatRepository chatRepository;

    /**
     * Service for user-related operations.
     */
    @Inject
    private UserService userService;

    /**
     * Creates a new chat between two users if it doesn't already exist.
     *
     * @param userId1 ID of the first user
     * @param userId2 ID of the second user
     * @return the newly created {@link Chat} entity
     * @throws IllegalArgumentException if both user IDs are the same or chat already exists
     */
    @Transactional
    public Chat create(final Long userId1, final Long userId2) {
        log.info("Creating new chat");
        Long first = Math.min(userId1, userId2);
        Long second = Math.max(userId1, userId2);
        if (first == second) {
            log.warn("First and Second chat id {} are the same", first);
            throw new IllegalArgumentException("first and second are the same");
        }
        if (existByUsersId(first, second)) {
            log.warn("Chat id {} already exists", first);
            throw new IllegalArgumentException("Chat already exists");
        }
        User user1 = userService.getById(first);
        User user2 = userService.getById(second);

        Chat chat = new Chat();
        chat.setUser1(user1);
        chat.setUser2(user2);

        chatRepository.persist(chat);
        log.info("Created chat with id: {}", chat.getId());
        return chat;
    }

    /**
     * Retrieves a chat by its ID.
     *
     * @param chatId ID of the chat to retrieve
     * @return the found {@link Chat} entity
     * @throws NotFoundException if no chat is found with the given ID
     */
    public Chat getById(final Long chatId) {
        Chat result = chatRepository.findById(chatId);
        if (result == null) {
            throw new NotFoundException("Chat with id " + chatId + " not found");
        }
        return result;
    }

    /**
     * Retrieves the list of messages in a given chat.
     *
     * @param chatId ID of the chat
     * @return list of {@link ChatMessage} in the chat
     */
    public List<ChatMessage> getHistory(final Long chatId) {
        log.info("Getting history for chat {}", chatId);
        Chat chat = getById(chatId);
        return chat.getMessages();
    }

    /**
     * Checks if a chat with the given ID exists.
     *
     * @param chatId ID of the chat
     * @return true if chat exists, false otherwise
     */
    public boolean existById(final Long chatId) {
        Chat chat = chatRepository.findById(chatId);
        if (chat == null) {
            log.warn("Chat with id {} not found", chatId);
            return false;
        }
        log.info("Chat with id {} found", chatId);
        return true;
    }

    /**
     * Checks if a chat exists between two users.
     *
     * @param userId1 ID of the first user
     * @param userId2 ID of the second user
     * @return true if a chat exists, false otherwise
     */
    public boolean existByUsersId(final Long userId1, final Long userId2) {
        Chat chat  = chatRepository.findByUsersId(userId1, userId2);
        if (chat == null) {
            log.warn("Chat with user id {} and {} not found", userId1, userId2);
            return false;
        }
        log.info("Chat with user id {} and {} found", userId1, userId2);
        return true;
    }

    public List<Chat> getByUserId(final Long userId) {
        return chatRepository.findByUserId(userId);
    }

}
