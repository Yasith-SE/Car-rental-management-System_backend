package edu.icet.model.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupportChatParticipant {
    private String sessionId;
    private String participantId;
    private String role;
    private String displayName;
    private String email;

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }
}
