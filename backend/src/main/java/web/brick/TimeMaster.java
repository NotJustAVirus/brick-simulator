package web.brick;

import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

import io.micronaut.websocket.WebSocketBroadcaster;
import io.micronaut.websocket.WebSocketSession;
import web.brick.message.TimeSyncMessage;
import web.brick.message.UserCountMessage;
import web.brick.message.UserMessage;

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
                        timeCounting = getTotalTimeElapsed();
                        long time = timeCounting + timeOld;
                        broadcaster.broadcastAsync(new TimeSyncMessage(time, true));

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
        session.put("user", user);
        UserMessage newUserMessage = new UserMessage(uuid);
        session.sendAsync(newUserMessage);
        TimeSyncMessage timeSyncMessage = new TimeSyncMessage(user.getTimeElapsed(System.currentTimeMillis()), false);
        session.sendAsync(timeSyncMessage);
        TimeSyncMessage timeSyncMessage2 = new TimeSyncMessage(timeCounting + timeOld, true);
        session.sendAsync(timeSyncMessage2);
        broadcaster.broadcastAsync(new UserCountMessage(user.sessions(), false), user(uuid));
        broadcastUserCount();
        return user;
    }

    private void clean() {
        for (User user : users.values()) {
            if (user.clean()) {
                long time = user.getTimeElapsed(0);
                if (DatabaseManager.getInstance().setUserTime(user.getUuid(), time)) {
                    users.remove(user.getUuid());
                    timeOld += time;
                }
            }
        }
    }

    private long getTotalTimeElapsed() {
        timeLast = System.currentTimeMillis();
        long totalTimeElapsed = 0;
        for (User user : users.values()) {
            long elapsed = user.getTimeElapsed(timeLast);
            totalTimeElapsed += elapsed;
            broadcaster.broadcastAsync(new TimeSyncMessage(elapsed, false), user(user.getUuid()));
        }
        return totalTimeElapsed;
    }

    private Predicate<WebSocketSession> user(String uuid) { 
        return (s) -> {
            try {
                return s.get("user", User.class).get().getUuid().equals(uuid);
            } catch (NoSuchElementException e) {
                return false;
            }
        };
    }

    public void removeSession(WebSocketSession session) {
        User user = session.get("user", User.class).get();
        user.removeSession(session.getId());
        broadcaster.broadcastAsync(new UserCountMessage(user.sessions(), false), user(user.getUuid()));
        broadcastUserCount();
    }

    public void broadcastUserCount() {
        int count = 0;
        for (User user : users.values()) {
            count += user.sessions();
        }
        broadcaster.broadcastAsync(new UserCountMessage(count, true));
    }
}
