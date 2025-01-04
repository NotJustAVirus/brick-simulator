package web.brick.message;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class TimeSyncMessage extends Message {
    private boolean isTotalTime;
    private long time;

    public TimeSyncMessage(long time, boolean isTotalTime) {
        super("timeSync");
        this.time = time;
        this.isTotalTime = isTotalTime;
    }

    public long getTime() {
        return time;
    }

    public boolean getIsTotalTime() {
        return isTotalTime;
    }
}
