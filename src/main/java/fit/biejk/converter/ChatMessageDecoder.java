package fit.biejk.converter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fit.biejk.dto.ChatInputMessage;
import jakarta.websocket.Decoder;
import lombok.SneakyThrows;

public class ChatMessageDecoder implements Decoder.Text<ChatInputMessage> {

    private final ObjectMapper jackson = ChatMessageDecoder.getJackson();

    @Override
    @SneakyThrows
    public ChatInputMessage decode(String s) {
        return jackson.readValue(s, ChatInputMessage.class);
    }

    @Override
    public boolean willDecode(String s) {
        return s != null;
    }

    public static ObjectMapper getJackson() {
        ObjectMapper om = new ObjectMapper();
        om.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        om.registerModule(new JavaTimeModule());
        return om;
    }
}

