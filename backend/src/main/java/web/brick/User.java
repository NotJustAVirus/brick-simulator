package web.brick;

import java.util.HashMap;

public class User {
    private String uuid;
    private long pastTime;
    private HashMap<String, Session> sessions;

    public User(String uuid, long pastTime) {
        this.uuid = uuid;
        this.pastTime = pastTime;
        this.sessions = new HashMap<>();
    }

    public void addSession(Session session) {
        sessions.put(session.getId(), session);
    }

    public String getUuid() {
        return uuid;
    }

    public void removeSession(String id) {
        pastTime += sessions.get(id).getTimeElapsed(System.currentTimeMillis());
        sessions.remove(id);
    }

    public long getTimeElapsed(long timeLast) {
        long timeElapsed = 0;
        for (Session session : sessions.values()) {
            timeElapsed += session.getTimeElapsed(timeLast);
        }
        return timeElapsed + pastTime;
    }

    public boolean clean() {
        for (Session session : sessions.values()) {
            if (session.isClosed()) {
                sessions.remove(session.getId());
            }
        }
        return sessions.isEmpty();
    }
}
