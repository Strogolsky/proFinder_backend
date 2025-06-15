package fit.biejk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChatResponse {
    private Long chatId;

    private Long partnerId;

    private String partnerFirstName;
}
