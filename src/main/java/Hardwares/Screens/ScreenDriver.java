package Hardwares.Screens;

public class ScreenDriver {
    private final Screen screen;

    public ScreenDriver(Screen screen) {
        this.screen = screen;
    }

    public void turnOn() {
        screen.screenOn();
    }

    public void turnOff() {
        screen.screenOff();
    }

    public void present(String template) {
        screen.presentTemplate(template);
    }

    public boolean isExitReady() {
        return screen.exitReady();
    }

    public String fetchTemplate() {
        return screen.returnTemplate();
    }

    public boolean hasFailure() {
        return screen.screenFailed();
    }

    public void setFailure(boolean status) {
        screen.setFailure(status);
    }

    public void simulateUserComplete() {
        screen.simulateUserDone();
    }
}
