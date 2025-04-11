package fit.biejk.repository;

import fit.biejk.entity.ChatMessage;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ChatMessageRepository implements PanacheRepository<ChatMessage> {
}
