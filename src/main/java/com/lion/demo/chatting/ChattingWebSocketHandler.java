package com.lion.demo.chatting;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChattingWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    private final Map<String, String> userStatus = new ConcurrentHashMap<>(); // 쓰레드에 안전한 해쉬맵

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = getUserId(session);
        String status = getStatus(session);

        if (userId != null) {
            userSessions.put(userId, session);
            userStatus.put(userId, status);
            System.out.println("User connected: " + userId + ", status:" + status) ;
        } else {
            System.out.println("User ID is null. Closing session.");
            session.close();
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String userId = getUserId(session);
        String payload = message.getPayload(); // recipientId : message
        String[] parts = payload.split(":",2);
//        System.out.println("Recieved message: " + payload);

        if (parts.length == 2) {
            String recipientId = parts[0];
            String msg = parts[1];

            WebSocketSession targetSession = userSessions.get(recipientId);
            String targetStatus = userStatus.get(recipientId); // "home", "chat:maria"
//            System.out.println("=========" + targetStatus);

            if (targetSession != null && targetSession.isOpen()) {
                if (targetStatus.equals("home") || targetStatus.equals("chat:" + userId)) {
                    targetSession.sendMessage(new TextMessage("from " + userId + ": " + msg));
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = getUserId(session);
        if (userId != null) {
            userSessions.remove(userId);
            System.out.println("User disconnected: " + userId);
        }

    }

    public int isReadable(String senderUid, String recipientUid) {
        WebSocketSession targetSession = userSessions.get(recipientUid);
        if (targetSession != null && targetSession.isOpen()) {
            String targetStatus = userStatus.get(recipientUid);
            if (targetStatus.equals("chat:" + senderUid)) {
                return 1;
            }
        }
        return 0;
    }

    private String getUserId(WebSocketSession session) {
        Object userId = session.getAttributes().get("userId");
        return userId != null ? userId.toString() : null;
    }

    private String getStatus(WebSocketSession session) {
        Object status = session.getAttributes().get("status");
        return status != null ? status.toString() : null;
    }
}
