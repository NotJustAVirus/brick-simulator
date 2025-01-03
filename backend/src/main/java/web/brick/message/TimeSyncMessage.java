package web.brick.message;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class TimeSyncMessage extends Message {
    private long time;
    private long totalTime;

    public TimeSyncMessage(long time, long totalTime) {
        super("timeSync");
        this.time = time;
        this.totalTime = totalTime;
    }

    public long getTime() {
        return time;
    }

    public long getTotalTime() {
        return totalTime;
    }
}
