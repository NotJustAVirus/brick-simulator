package web.brick.message;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class UserMessage extends Message {
    private String uuid;
    
    public UserMessage(String uuid) {
        super("user");
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }
}
