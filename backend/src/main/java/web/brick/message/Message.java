package web.brick.message;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class Message {
    private String message;

    public Message(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
