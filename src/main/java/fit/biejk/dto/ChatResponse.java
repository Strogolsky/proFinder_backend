package fit.biejk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for displaying chat information in the chat list.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChatResponse {

    /** Unique ID of the chat. */
    private Long chatId;

    /** ID of the other user in the chat. */
    private Long partnerId;

    /** First name of the chat partner. */
    private String partnerFirstName;
}
