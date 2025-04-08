package fit.biejk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatOutputMessage {
    private String content;
    private Long fromId;
    private LocalDateTime timestamp = LocalDateTime.now();
}
