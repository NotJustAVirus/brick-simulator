package web.brick;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class UserCountMessage {
    private int userCount;

    public UserCountMessage(int userCount) {
        this.userCount = userCount;
    }

    public int getUserCount() {
        return userCount;
    }
}
