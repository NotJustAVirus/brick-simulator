package web.brick;

import java.util.HashMap;
import java.util.function.Predicate;

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
        this.timeOld = DatabaseManager.getInstance().getTotalTime();
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
                    try {
                        update();
                        long time = timeCounting + timeOld;
                        broadcaster.broadcastSync(new TimeSyncMessage(time, true));

                        clean();

                    } catch (Exception e) {}

                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public User addSession(WebSocketSession session, String uuid) {
        User user = users.get(uuid);
        if (user == null) {
            long time = DatabaseManager.getInstance().getUserTime(uuid);
            if (time != -1) {
                timeOld -= time;
                user = new User(uuid, time);
            } else {
                user = new User(uuid, 0);
            }
            users.put(uuid, user);
        }
        user.addSession(new Session(session));
        return user;
    }

    private void clean() {
        for (User user : users.values()) {
            if (user.clean()) {
                users.remove(user.getUuid());
                long time = user.getTimeElapsed(0);
                DatabaseManager.getInstance().setUserTime(user.getUuid(), time);
                timeOld += time;
            }
        }
    }

    private void update() {
        timeLast = System.currentTimeMillis();
        timeCounting = 0;
        for (User user : users.values()) {
            long elapsed = user.getTimeElapsed(timeLast);
            timeCounting += elapsed;
            broadcaster.broadcastSync(new TimeSyncMessage(elapsed, false), user(user.getUuid()));
        }
    }

    private Predicate<WebSocketSession> user(String uuid) { 
        return s -> s.get("user", User.class).get().getUuid().equals(uuid);
    }
}
