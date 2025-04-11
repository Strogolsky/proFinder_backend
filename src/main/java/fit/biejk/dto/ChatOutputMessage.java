package fit.biejk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatOutputMessage {
    private Long id;
    private Long chatId;
    private String content;
    private Long senderId;
    private LocalDateTime createAt;
}
