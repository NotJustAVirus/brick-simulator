package web.brick;

import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.runtime.server.event.ServerShutdownEvent;
import io.micronaut.serde.ObjectMapper;
import io.micronaut.websocket.WebSocketBroadcaster;
import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.OnClose;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import io.micronaut.websocket.annotation.ServerWebSocket;
import web.brick.message.Message;
import web.brick.message.UserMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@ServerWebSocket("/ws/") 
public class ConnectWebSocket {
    private static final Logger LOG = LoggerFactory.getLogger(ConnectWebSocket.class);
    
    private final WebSocketBroadcaster broadcaster;
    private final TimeMaster timeMaster;
    
    public ConnectWebSocket(WebSocketBroadcaster broadcaster) { 
        this.broadcaster = broadcaster;
        this.timeMaster = new TimeMaster(broadcaster);
        timeMaster.start();
    }

    @OnOpen 
    public void onOpen(WebSocketSession session) {
        log("onOpen", session);
        return;
    }

    @OnMessage 
    public void onMessage(String message, WebSocketSession session) {
        log("onMessage", session);
        ObjectMapper objectMapper = ObjectMapper.getDefault();
        try {
            Message msg = objectMapper.readValue(message, Message.class);
            if (msg == null) { return; }
            if (msg.getMessage().equals("user")) {
                UserMessage userMessage = objectMapper.readValue(message, UserMessage.class);
                String uuidStr = userMessage.getUuid();
                if (uuidStr == null) {
                    UUID uuid = UUID.randomUUID();
                    uuidStr = uuid.toString();
                }
                timeMaster.addSession(session, uuidStr);
            }
        } catch (Exception e) {
            log("unable to parse message", session);
            e.printStackTrace();
        }
        return;
    }

    @OnClose 
    public void onClose(WebSocketSession session) {
        log("onClose", session);
        timeMaster.removeSession(session);
        return;
    }

    @EventListener
    public void onShutdownEvent(ServerShutdownEvent event) {
        timeMaster.shutdown();
    }

    private void log(String event, WebSocketSession session) {
        LOG.info("* WebSocket: {} on session {}",
            event, session.getId());
    }
}