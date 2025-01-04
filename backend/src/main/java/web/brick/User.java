package web.brick;

import io.micronaut.websocket.WebSocketSession;
import web.brick.message.TimeSyncMessage;

public class User {
    private WebSocketSession session;
    private String id;
    private long timeJoined;
    private long timeElapsed;
    private long oldTime;

    public User(String id, WebSocketSession session) {
        this.id = id;
        this.timeJoined = System.currentTimeMillis();
        this.session = session;
    }

    public void syncTime() {
        TimeSyncMessage message = new TimeSyncMessage(timeElapsed + oldTime, false);
        session.sendAsync(message);
    }

    public long update(long timeNow) {
        timeElapsed = timeNow - timeJoined;
        return timeElapsed;
    }

    public long getTotalTime() {
        return System.currentTimeMillis() - timeJoined + oldTime;
    }

    @Override 
    public boolean equals(Object obj) {
        if (obj instanceof User) {
            return ((User) obj).id.equals(id);
        }
        return false;
    }

    public String getId() {
        return id;
    }

    public void setOldTime(long oldTime) {
        this.oldTime = oldTime;
    }
}