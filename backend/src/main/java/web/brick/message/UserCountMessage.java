package web.brick.message;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class UserCountMessage extends Message {
    private boolean isTotal;
    private int userCount;

    public UserCountMessage(int userCount, boolean isTotal) {
        super("userCount");
        this.userCount = userCount;
        this.isTotal = isTotal;
    }

    public int getUserCount() {
        return userCount;
    }

    public boolean getIsTotal() {
        return isTotal;
    }
}
