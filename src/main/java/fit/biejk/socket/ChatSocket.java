package fit.biejk.socket;

import fit.biejk.converter.ChatMessageDecoder;
import fit.biejk.converter.ChatMessageEncoder;
import fit.biejk.dto.ChatInputMessage;
import fit.biejk.dto.ChatOutputMessage;
import fit.biejk.entity.ChatMessage;
import fit.biejk.mapper.ChatMessageMapper;
import fit.biejk.repository.ChatMessageService;
import fit.biejk.service.ChatService;
import fit.biejk.service.UserService;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.context.ManagedExecutor;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ApplicationScoped
@ServerEndpoint(
        value = "/chat/{chatId}",
        decoders = ChatMessageDecoder.class,
        encoders = ChatMessageEncoder.class
)
public class ChatSocket {

    @Inject
    JWTParser jwtParser;

    @Inject
    ChatMessageService chatMessageService;

    @Inject
    ManagedExecutor executor;

    @Inject
    ChatService chatService;

    @Inject
    ChatMessageMapper chatMessageMapper;

    @Inject
    UserService userService;

    private static final Map<Long, Set<Session>> chatSessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("chatId") Long chatId) {

        String token = getTokenFromQuery(session);
        if (token == null) {
            closeSession(session, "Unauthorized");
            return;
        }

        executor.execute(() -> {
            Long userId = extractUserId(token);

            if (userId == null || !userService.existById(userId)) {
                closeSession(session, "Unauthorized");
                return;
            }

            if(!chatService.existById(chatId)) {
                closeSession(session, "Chat is not exist");
                return;
            }

            session.getUserProperties().put("userId", userId);

            chatSessions
                    .computeIfAbsent(chatId, k -> ConcurrentHashMap.newKeySet())
                    .add(session);

            log.info("User {} joined chat {}", userId, chatId);
        });
    }


    @OnMessage
    public void onMessage(ChatInputMessage inputMessage, Session session, @PathParam("chatId") Long chatId) {
        Long senderId = (Long) session.getUserProperties().get("userId");
        if (senderId == null) {
            log.warn("No userId associated with session: {}", session.getId());
            return;
        }

        executor.execute(() -> {

            if(!userService.existById(senderId)) {
                return;
            }

            try {
                ChatMessage message = chatMessageService.create(chatId, senderId, inputMessage.getContent());

                ChatOutputMessage outputMessage = chatMessageMapper.toDto(message);
                log.info("Sending message in chat {}: {}", chatId, outputMessage);

                Set<Session> sessions = chatSessions.getOrDefault(chatId, Set.of());
                for (Session s : sessions) {
                    if (s.isOpen()) {
                        s.getAsyncRemote().sendObject(outputMessage);
                    }
                }
            } catch (Exception e) {
                log.error("Failed to process message", e);
            }
        });
    }

    @OnClose
    public void onClose(Session session, @PathParam("chatId") Long chatId) {
        Set<Session> sessions = chatSessions.get(chatId);
        if (sessions != null) {
            sessions.remove(session);
        }
        log.info("User disconnected from chat {}: {}", chatId, session.getId());
    }

    @OnError
    public void onError(Session session, Throwable error, @PathParam("chatId") Long chatId) {
        log.warn("Error in chat {}: {}", chatId, error.getMessage());
    }

    // --- Helpers ---

    private String getTokenFromQuery(Session session) {
        String query = session.getQueryString();
        if (query == null) return null;
        for (String param : query.split("&")) {
            if (param.startsWith("token=")) {
                return param.substring("token=".length());
            }
        }
        return null;
    }

    private void closeSession(Session session, String reason) {
        try {
            session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, reason));
        } catch (Exception ignored) {}
        log.warn("Closed session {}: {}", session.getId(), reason);
    }

    public Long extractUserId(String token) {
        try {
            JsonWebToken jwt = jwtParser.parse(token);
            return Long.parseLong(jwt.getSubject());
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid JWT", e);
        }
    }
}
