package Hardwares;

import Hardwares.latch.LatchDriver;

public class Monitor implements Runnable {
    private final LatchDriver latchDriver;
    private volatile boolean running = true;

    public Monitor(LatchDriver latchDriver) {
        this.latchDriver = latchDriver;
    }

    public void stopMonitoring() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            if (latchDriver.hasFailure()) {
                System.out.println("[Monitor] Latch failure detected!");
            } else {
                System.out.println("[Monitor] Latch system is functioning normally.");
            }

            try {
                Thread.sleep(2000); // check every 2 seconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}