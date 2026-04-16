package edu.icet.model.chat;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
@NoArgsConstructor
public class SupportChatConversation {
    private String sessionId;
    private String customerName = "Customer";
    private String customerId = "";
    private boolean online;
    private int unreadCount;
    private String lastMessage = "";
    private String updatedAt = "";
    private List<SupportChatMessage> messages = new CopyOnWriteArrayList<>();

    public SupportChatConversation(String sessionId) {
        this.sessionId = sessionId;
    }
}
