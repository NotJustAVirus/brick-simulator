package web.brick.message;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class UserCountMessage extends Message {
    private int userCount;

    public UserCountMessage(int userCount) {
        super("userCount");
        this.userCount = userCount;
    }

    public int getUserCount() {
        return userCount;
    }
}
