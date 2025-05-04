package Hardwares.latch;

public class LatchDriver {
    private Latches latch;

    public LatchDriver(Latches latch) {
        this.latch = latch;
    }

    public boolean latch() {
        return latch.latch();
    }

    public boolean unlatch() {
        return latch.unlatch();
    }

    public boolean isLatched() {
        return latch.isLatched();
    }

    public boolean hasFailure() {
        return latch.failure();
    }

    public boolean openDoor() {
        return latch.openDoor();
    }

    public boolean closeDoor() {
        return latch.closeDoor();
    }
}
