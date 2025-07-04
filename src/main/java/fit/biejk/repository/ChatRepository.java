package fit.biejk.repository;

import fit.biejk.entity.Chat;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

/**
 * Repository class for managing {@link Chat} entities.
 * <p>
 * Provides database operations for Chat entities using Panache.
 * </p>
 */
@ApplicationScoped
public class ChatRepository implements PanacheRepository<Chat> {

    /**
     * Finds a chat by the IDs of two users.
     * <p>
     * Ensures consistent ordering of user IDs to match unique constraint.
     * </p>
     *
     * @param userId1 ID of the first user
     * @param userId2 ID of the second user
     * @return The {@link Chat} entity if found, otherwise {@code null}
     */
    public Chat findByUsersId(final Long userId1, final Long userId2) {
        Long first = Math.min(userId1, userId2);
        Long second = Math.max(userId1, userId2);
        return find("user1.id = ?1 AND user2.id = ?2", first, second).firstResult();
    }

    /**
     * Finds all chats where the given user is a participant.
     *
     * @param userId ID of the user
     * @return list of {@link Chat} entities
     */
    public List<Chat> findByUserId(final Long userId) {
        return find("user1.id = ?1 OR user2.id = ?1", userId).list();
    }
}
