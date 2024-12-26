package web.brick;

import java.util.ArrayList;

import io.micronaut.websocket.WebSocketSession;

public class TimeMaster {
    private long timeOld;
    private long timeCounting;
    private long timeLast;
    private ArrayList<User> users;
    private int id = 0;

    public TimeMaster() {
        this.timeOld = 0;
        this.timeCounting = 0;
        this.timeLast = System.currentTimeMillis();
        this.users = new ArrayList<>();
    }

    public void start() {
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    update();
                    long time = timeCounting + timeOld;
                    for (User user : users) {
                        user.syncTime(time);
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public User newUser(WebSocketSession session) {
        User user = new User(id++, session);
        users.add(user);
        return user;
    }

    public void removeUser(User user) {
        timeOld += user.getTimeSinceJoined();
        users.remove(user);
    }

    private void update() {
        timeLast = System.currentTimeMillis();
        timeCounting = 0;
        for (User user : users) {
            timeCounting += user.update(timeLast);
        }
    }
}
