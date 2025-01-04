package web.brick;

import io.micronaut.websocket.WebSocketSession;

public class Session {
    private WebSocketSession session;
    private long timeJoined;

    public Session(WebSocketSession session) {
        this.timeJoined = System.currentTimeMillis();
        this.session = session;
    }

    public long getTimeElapsed(long time) {
        return time - timeJoined;
    }

    public String getId() {
        return session.getId();
    }

    public boolean isClosed() {
        return !session.isOpen();
    }
}