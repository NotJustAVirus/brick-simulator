package web.brick;

import io.micronaut.websocket.WebSocketBroadcaster;
import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.OnClose;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import io.micronaut.websocket.annotation.ServerWebSocket;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Predicate;

@ServerWebSocket("/ws/") 
public class ConnectWebSocket {
    private static final Logger LOG = LoggerFactory.getLogger(ConnectWebSocket.class);
    
    private final WebSocketBroadcaster broadcaster;
    private final TimeMaster timeMaster = new TimeMaster();
    
    public ConnectWebSocket(WebSocketBroadcaster broadcaster) { 
        this.broadcaster = broadcaster;
        timeMaster.start();
    }

    @OnOpen 
    public void onOpen(WebSocketSession session) {
        log("onOpen", session);
        // set cookie
        User user = timeMaster.newUser(session);
        session.put("user", user);
        return;
    }

    @OnMessage 
    public void onMessage(String message, WebSocketSession session) {
        log("onMessage", session);
        // session.sendSync(System.currentTimeMillis());
        return;
    }

    @OnClose 
    public void onClose(WebSocketSession session) {
        log("onClose", session);
        timeMaster.removeUser(session.get("user", User.class).get());
        return;
    }

    private void log(String event, WebSocketSession session) {
        LOG.info("* WebSocket: {} received for session {} from '{}' regarding '{}'",
            event, session.getId(), "username", "topic");
    }

    private Predicate<WebSocketSession> isValid() { 
        return s -> true;
    }
}