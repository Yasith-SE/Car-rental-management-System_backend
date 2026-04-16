package edu.icet.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.icet.model.chat.SupportChatConversation;
import edu.icet.model.chat.SupportChatMessage;
import edu.icet.model.chat.SupportChatParticipant;
import edu.icet.model.entity.User;
import edu.icet.repository.UserRepository;
import edu.icet.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class SupportChatWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final Map<String, WebSocketSession> liveSessions = new ConcurrentHashMap<>();
    private final Map<String, SupportChatParticipant> participantsBySocketId = new ConcurrentHashMap<>();
    private final Map<String, SupportChatConversation> conversations = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> customerSocketIdsByConversation = new ConcurrentHashMap<>();
    private final Set<String> adminSocketIds = ConcurrentHashMap.newKeySet();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        liveSessions.put(session.getId(), session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JsonNode root;

        try {
            root = objectMapper.readTree(message.getPayload());
        } catch (IOException exception) {
            sendError(session, "Invalid support chat payload.");
            return;
        }

        String type = textValue(root, "type");
        JsonNode payload = root.path("payload");

        switch (type) {
            case "chat:join" -> handleJoin(session, payload);
            case "chat:sessions:request" -> handleSessionsRequest(session);
            case "chat:history:request" -> handleHistoryRequest(session, payload);
            case "chat:message" -> handleIncomingMessage(session, payload);
            default -> sendError(session, "Unsupported support chat action: " + type);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        liveSessions.remove(session.getId());

        SupportChatParticipant participant = participantsBySocketId.remove(session.getId());
        if (participant == null) {
            return;
        }

        if (participant.isAdmin()) {
            adminSocketIds.remove(session.getId());
            return;
        }

        Set<String> socketIds = customerSocketIdsByConversation.getOrDefault(
                participant.getSessionId(),
                ConcurrentHashMap.newKeySet()
        );
        socketIds.remove(session.getId());

        if (socketIds.isEmpty()) {
            customerSocketIdsByConversation.remove(participant.getSessionId());
            SupportChatConversation conversation = conversations.get(participant.getSessionId());
            if (conversation != null) {
                conversation.setOnline(false);
            }
        }

        broadcastSessionsToAdmins();
    }

    private void handleJoin(WebSocketSession session, JsonNode payload) throws IOException {
        User authenticatedUser = resolveAuthenticatedUser(session);
        SupportChatParticipant participant;

        if (authenticatedUser != null) {
            String role = normalizeRole(authenticatedUser.getRole());
            String fallbackSessionId = role.equals("ADMIN")
                    ? "admin-" + authenticatedUser.getId()
                    : "customer-" + authenticatedUser.getId();

            participant = new SupportChatParticipant(
                    nonBlank(textValue(payload, "sessionId"), fallbackSessionId),
                    String.valueOf(authenticatedUser.getId()),
                    role,
                    nonBlank(authenticatedUser.getName(), role.equals("ADMIN") ? "Showroom Admin" : "Customer"),
                    authenticatedUser.getEmail()
            );
        } else {
            String requestedRole = upperTextValue(payload, "role", "CUSTOMER");

            if ("ADMIN".equalsIgnoreCase(requestedRole)) {
                sendError(session, "Admin chat requires a valid signed-in admin token.");
                session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Admin token required"));
                return;
            }

            participant = new SupportChatParticipant(
                    nonBlank(textValue(payload, "sessionId"), session.getId()),
                    nonBlank(textValue(payload, "participantId"), session.getId()),
                    "CUSTOMER",
                    nonBlank(textValue(payload, "displayName"), "Customer"),
                    textValue(payload, "email")
            );
        }

        participantsBySocketId.put(session.getId(), participant);

        if (participant.isAdmin()) {
            adminSocketIds.add(session.getId());
            sendSessions(session);
            return;
        }

        customerSocketIdsByConversation
                .computeIfAbsent(participant.getSessionId(), key -> ConcurrentHashMap.newKeySet())
                .add(session.getId());

        SupportChatConversation conversation = conversations.computeIfAbsent(
                participant.getSessionId(),
                SupportChatConversation::new
        );
        conversation.setCustomerName(participant.getDisplayName());
        conversation.setCustomerId(participant.getParticipantId());
        conversation.setOnline(true);

        if (isBlank(conversation.getUpdatedAt())) {
            conversation.setUpdatedAt(Instant.now().toString());
        }

        sendHistory(session, conversation);
        broadcastSessionsToAdmins();
    }

    private void handleSessionsRequest(WebSocketSession session) throws IOException {
        SupportChatParticipant participant = participantsBySocketId.get(session.getId());

        if (participant == null || !participant.isAdmin()) {
            sendError(session, "Only admins can request the support chat session list.");
            return;
        }

        sendSessions(session);
    }

    private void handleHistoryRequest(WebSocketSession session, JsonNode payload) throws IOException {
        SupportChatParticipant participant = participantsBySocketId.get(session.getId());

        if (participant == null) {
            sendError(session, "Join the support chat before requesting history.");
            return;
        }

        String requestedSessionId = textValue(payload, "sessionId");
        String conversationId = participant.isAdmin()
                ? nonBlank(requestedSessionId, "")
                : participant.getSessionId();

        if (isBlank(conversationId)) {
            sendError(session, "Conversation session id is required.");
            return;
        }

        SupportChatConversation conversation = conversations.computeIfAbsent(
                conversationId,
                SupportChatConversation::new
        );

        if (!participant.isAdmin()) {
            conversation.setCustomerName(participant.getDisplayName());
            conversation.setCustomerId(participant.getParticipantId());
            conversation.setOnline(true);
        }

        sendHistory(session, conversation);
    }

    private void handleIncomingMessage(WebSocketSession session, JsonNode payload) throws IOException {
        SupportChatParticipant participant = participantsBySocketId.get(session.getId());

        if (participant == null) {
            sendError(session, "Join the support chat before sending messages.");
            return;
        }

        String text = textValue(payload, "text").trim();
        if (text.isEmpty()) {
            sendError(session, "Chat messages cannot be empty.");
            return;
        }

        String conversationId = participant.isAdmin()
                ? textValue(payload, "sessionId")
                : participant.getSessionId();

        if (isBlank(conversationId)) {
            sendError(session, "Conversation session id is required.");
            return;
        }

        SupportChatConversation conversation = conversations.computeIfAbsent(
                conversationId,
                SupportChatConversation::new
        );

        if (!participant.isAdmin()) {
            conversation.setCustomerName(participant.getDisplayName());
            conversation.setCustomerId(participant.getParticipantId());
            conversation.setOnline(true);
        }

        String timestamp = nonBlank(textValue(payload, "timestamp"), Instant.now().toString());
        String senderRole = participant.isAdmin() ? "ADMIN" : "CUSTOMER";
        SupportChatMessage chatMessage = new SupportChatMessage(
                UUID.randomUUID().toString(),
                nonBlank(textValue(payload, "clientMessageId"), UUID.randomUUID().toString()),
                conversationId,
                text,
                senderRole,
                nonBlank(textValue(payload, "senderId"), participant.getParticipantId()),
                nonBlank(textValue(payload, "senderName"), participant.getDisplayName()),
                timestamp,
                "sent"
        );

        conversation.getMessages().add(chatMessage);
        conversation.setLastMessage(chatMessage.getText());
        conversation.setUpdatedAt(chatMessage.getTimestamp());

        broadcastMessage(chatMessage, conversation);
        broadcastSessionsToAdmins();
    }

    private void sendSessions(WebSocketSession session) throws IOException {
        List<Map<String, Object>> conversationSnapshots = conversations.values().stream()
                .sorted(Comparator.comparing(
                        SupportChatConversation::getUpdatedAt,
                        Comparator.nullsLast(String::compareTo)
                ).reversed())
                .map(this::toConversationPayload)
                .toList();

        sendEvent(session, "chat:sessions", Map.of("conversations", conversationSnapshots));
    }

    private void sendHistory(WebSocketSession session, SupportChatConversation conversation) throws IOException {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sessionId", conversation.getSessionId());
        payload.put("customerName", conversation.getCustomerName());
        payload.put("customerId", conversation.getCustomerId());
        payload.put("updatedAt", conversation.getUpdatedAt());
        payload.put("messages", new ArrayList<>(conversation.getMessages()));

        sendEvent(session, "chat:history", payload);
    }

    private void broadcastMessage(SupportChatMessage message, SupportChatConversation conversation) throws IOException {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("id", message.getId());
        payload.put("clientMessageId", message.getClientMessageId());
        payload.put("sessionId", message.getSessionId());
        payload.put("text", message.getText());
        payload.put("senderRole", message.getSenderRole());
        payload.put("senderId", message.getSenderId());
        payload.put("senderName", message.getSenderName());
        payload.put("timestamp", message.getTimestamp());
        payload.put("deliveryState", message.getDeliveryState());
        payload.put("customerName", conversation.getCustomerName());
        payload.put("customerId", conversation.getCustomerId());

        sendEventToSocketIds(adminSocketIds, "chat:message", payload);
        sendEventToSocketIds(
                customerSocketIdsByConversation.getOrDefault(message.getSessionId(), ConcurrentHashMap.newKeySet()),
                "chat:message",
                payload
        );
    }

    private void broadcastSessionsToAdmins() {
        if (adminSocketIds.isEmpty()) {
            return;
        }

        for (String socketId : adminSocketIds) {
            WebSocketSession adminSession = liveSessions.get(socketId);
            if (adminSession == null || !adminSession.isOpen()) {
                continue;
            }

            try {
                sendSessions(adminSession);
            } catch (IOException ignored) {
                // Ignore one failed admin broadcast and continue with the rest.
            }
        }
    }

    private Map<String, Object> toConversationPayload(SupportChatConversation conversation) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sessionId", conversation.getSessionId());
        payload.put("customerName", conversation.getCustomerName());
        payload.put("customerId", conversation.getCustomerId());
        payload.put("online", conversation.isOnline());
        payload.put("unreadCount", conversation.getUnreadCount());
        payload.put("lastMessage", conversation.getLastMessage());
        payload.put("updatedAt", conversation.getUpdatedAt());
        payload.put("messages", new ArrayList<>(conversation.getMessages()));
        return payload;
    }

    private void sendEventToSocketIds(Set<String> socketIds, String type, Object payload) throws IOException {
        for (String socketId : socketIds) {
            WebSocketSession targetSession = liveSessions.get(socketId);
            if (targetSession == null || !targetSession.isOpen()) {
                continue;
            }

            sendEvent(targetSession, type, payload);
        }
    }

    private void sendEvent(WebSocketSession session, String type, Object payload) throws IOException {
        if (session == null || !session.isOpen()) {
            return;
        }

        Map<String, Object> envelope = new LinkedHashMap<>();
        envelope.put("type", type);
        envelope.put("payload", payload);

        synchronized (session) {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(envelope)));
        }
    }

    private void sendError(WebSocketSession session, String message) throws IOException {
        sendEvent(session, "chat:error", Map.of("message", message));
    }

    private String textValue(JsonNode node, String fieldName) {
        if (node == null || node.isMissingNode() || node.path(fieldName).isMissingNode()) {
            return "";
        }

        return node.path(fieldName).asText("");
    }

    private String upperTextValue(JsonNode node, String fieldName, String fallback) {
        String value = textValue(node, fieldName);
        return nonBlank(value, fallback).toUpperCase();
    }

    private String nonBlank(String value, String fallback) {
        return isBlank(value) ? fallback : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private User resolveAuthenticatedUser(WebSocketSession session) {
        String token = extractTokenFromQuery(session);

        if (isBlank(token)) {
            return null;
        }

        String subject = jwtService.extractSubject(token);

        if (isBlank(subject)) {
            return null;
        }

        try {
            Long userId = Long.parseLong(subject);
            return userRepository.findById(userId)
                    .filter(user -> "APPROVED".equalsIgnoreCase(user.getAccessStatus()))
                    .filter(user -> jwtService.isTokenValid(token, user))
                    .orElse(null);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private String extractTokenFromQuery(WebSocketSession session) {
        if (session.getUri() == null || isBlank(session.getUri().getQuery())) {
            return "";
        }

        String[] queryPairs = session.getUri().getQuery().split("&");

        for (String pair : queryPairs) {
            String[] parts = pair.split("=", 2);
            if (parts.length == 2 && "token".equals(parts[0])) {
                return URLDecoder.decode(parts[1], StandardCharsets.UTF_8);
            }
        }

        return "";
    }

    private String normalizeRole(String role) {
        return "ADMIN".equalsIgnoreCase(role) ? "ADMIN" : "CUSTOMER";
    }
}
