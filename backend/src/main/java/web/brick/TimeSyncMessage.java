package web.brick;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class TimeSyncMessage {
    private long time;
    private long totalTime;

    public TimeSyncMessage(long time, long totalTime) {
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
