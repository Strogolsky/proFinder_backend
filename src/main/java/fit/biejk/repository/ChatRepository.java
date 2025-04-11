package fit.biejk.repository;

import fit.biejk.entity.Chat;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ChatRepository implements PanacheRepository<Chat> {
    public Chat findByUsersId(Long userId1, Long userId2) {
        Long first = Math.min(userId1, userId2);
        Long second = Math.max(userId1, userId2);
        return find("user1.id = ?1 AND user2.id = ?2", first, second).firstResult();
    }
}
