package Hardwares.Screens;

import Display.Template;

public class ScreenDriver {
    private static ScreenDriver instance;

    private String receivedMessage = "";
    private final Screen screen;

    public ScreenDriver(Screen screen) {
        this.screen = screen;
        instance = this; // Register singleton instance
    }

    public static ScreenDriver getInstance() {
        return instance;
    }

    public void turnOn() {
        screen.screenOn();
    }

    public void turnOff() {
        screen.screenOff();
    }

    public void present(Template template) {
        screen.presentTemplate(template);
    }

    public boolean isExitReady() {
        return screen.exitReady();
    }

    public Template fetchTemplate() {
        return screen.returnTemplate();
    }

    public void readExternalMessage(String input) {
        if (input.toLowerCase().startsWith("scd")) {
            this.receivedMessage = input.substring(3).trim();  // remove "scd" prefix
            System.out.println("[ScreenDriver Received: " + this.receivedMessage);
        }
    }

    public String getLastMessage() {
        return this.receivedMessage;
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
