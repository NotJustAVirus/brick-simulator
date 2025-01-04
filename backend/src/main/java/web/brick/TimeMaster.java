package web.brick;

import java.util.HashMap;

import io.micronaut.websocket.WebSocketSession;

public class TimeMaster {
    private long timeOld;
    private long timeCounting;
    private long timeLast;
    private HashMap<String, User> users;

    public TimeMaster() {
        this.timeOld = 0;
        this.timeCounting = 0;
        this.timeLast = System.currentTimeMillis();
        this.users = new HashMap<>();
    }

    public void start() {
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    update();
                    long time = timeCounting + timeOld;
                    for (User user : users.values()) {
                        try {
                            user.syncTime(time);
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
