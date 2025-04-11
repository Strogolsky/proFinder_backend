package fit.biejk.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import fit.biejk.dto.ChatOutputMessage;
import jakarta.websocket.EncodeException;
import jakarta.websocket.Encoder;
import lombok.SneakyThrows;

/**
 * WebSocket encoder that serializes {@link ChatOutputMessage} instances to JSON strings.
 * <p>
 * Uses Jackson's {@link ObjectMapper} to handle the conversion and date-time formatting.
 * </p>
 */
public class ChatMessageEncoder implements Encoder.Text<ChatOutputMessage> {

    /**
     * Configured Jackson object mapper for serialization.
     */
    private final ObjectMapper jackson = ChatMessageDecoder.getJackson();

    /**
     * Serializes a {@link ChatOutputMessage} to its JSON string representation.
     *
     * @param chatOutputMessage the message to encode
     * @return JSON string representing the message
     * @throws EncodeException if encoding fails
     */
    @Override
    @SneakyThrows
    public String encode(final ChatOutputMessage chatOutputMessage) throws EncodeException {
        return jackson.writeValueAsString(chatOutputMessage);
    }
}
