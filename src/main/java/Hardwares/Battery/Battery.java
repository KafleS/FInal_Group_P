package Hardwares.Battery;

public class Battery {
    private boolean status = false;
    public Battery() {}
    public boolean failure() {
        return status;
    }

    // Demo functions below
    public void setFailure(boolean status) {
        this.status = status;
    }
}
