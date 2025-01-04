package web.brick;

import java.util.HashMap;

import io.micronaut.websocket.WebSocketBroadcaster;
import io.micronaut.websocket.WebSocketSession;
import web.brick.message.TimeSyncMessage;

public class TimeMaster {
    private long timeOld;
    private long timeCounting;
    private long timeLast;
    private HashMap<String, User> users;
    private final WebSocketBroadcaster broadcaster;

    public TimeMaster(WebSocketBroadcaster broadcaster) {
        this.timeOld = 0;
        this.timeCounting = 0;
        this.timeLast = System.currentTimeMillis();
        this.users = new HashMap<>();
        this.broadcaster = broadcaster;
    }

    public void start() {
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    update();
                    long time = timeCounting + timeOld;
                    broadcaster.broadcastSync(new TimeSyncMessage(time, true));
                    for (User user : users.values()) {
                        try {
                            user.syncTime();
                        } catch (Exception e) {}
                    }

                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public User newUser(WebSocketSession session, String id) {
        User user = new User(id, session);
        users.put(id, user);
        return user;
    }

    public void removeUser(User user) {
        timeOld += user.getTimeSinceJoined();
        users.remove(user.getId());
    }

    private void update() {
        timeLast = System.currentTimeMillis();
        timeCounting = 0;
        for (User user : users.values()) {
            timeCounting += user.update(timeLast);
        }
    }
}
