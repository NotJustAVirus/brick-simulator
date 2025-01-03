package web.brick.message;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class SessionMessage extends Message {
    private String session;
    
    public SessionMessage(String session) {
        super("session");
        this.session = session;
    }

    public String getSession() {
        return session;
    }
}
