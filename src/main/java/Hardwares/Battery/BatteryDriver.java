package Hardwares.Battery;

public class BatteryDriver {
    private final Battery battery;

    public BatteryDriver(Battery battery) {
        this.battery = battery;
    }

    public boolean hasFailed() {
        return battery.failure();
    }
}
