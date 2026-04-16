package edu.icet.model.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportChatMessage {
    private String id;
    private String clientMessageId;
    private String sessionId;
    private String text;
    private String senderRole;
    private String senderId;
    private String senderName;
    private String timestamp;
    private String deliveryState;
}
