package fit.biejk.repository;

import fit.biejk.entity.ChatMessage;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

/**
 * Repository class for managing {@link ChatMessage} entities.
 * <p>
 * Provides access to basic CRUD operations using Panache.
 * Additional custom queries can be added here as needed.
 * </p>
 */
@ApplicationScoped
public class ChatMessageRepository implements PanacheRepository<ChatMessage> {

    public List<ChatMessage> findByChatId(Long chatId, int page, int size) {
        return find("chat.id = ?1 ORDER BY createdAt DESC", chatId)
                .page(page - 1, size)
                .list();
    }
}
