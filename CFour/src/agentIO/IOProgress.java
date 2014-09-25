package agentIO;

public class IOProgress {
    private final long estimated;

    private volatile long actual;

    public IOProgress(long estimated) {
        this.estimated = estimated;
    }

    public long getEstimated() {
        return estimated;
    }

    public long getActual() {
        return actual;
    }

    public void update(long step) {
        this.actual += step;
    }

    public float get() {
        return actual * 1f / estimated;
    }
}