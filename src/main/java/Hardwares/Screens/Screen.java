package Hardwares.Screens;

public class Screen {
    private boolean isOn = false;
    private boolean status = false;
    private String currentTemplate = null;
    private boolean userReady = false;

    public void screenOn() {
        if (!status) {
            isOn = true;
            System.out.println("[Screen] Powered ON.");
        } else {
            System.out.println("[Screen] Cannot power on: screen failure detected.");
        }
    }

    public void presentTemplate(String template) {
        if (!status && isOn) {
            currentTemplate = template;
            userReady = false;
            System.out.println("[Screen] Presenting template: " + template);
        } else {
            System.out.println("[Screen] Cannot present template. Screen is either off or failed.");
        }
    }

    public boolean exitReady() {
        return userReady;
    }

    public String returnTemplate() {
        if (!status && currentTemplate != null) {
            return currentTemplate + " (user-modified)";
        }
        return null;
    }

    public boolean screenFailed() {
        return status;
    }

    public void screenOff() {
        isOn = false;
        currentTemplate = null;
        System.out.println("[Screen] Powered OFF.");
    }

    public void setFailure(boolean status) {
        this.status = status;
        if (status) System.out.println("[Screen] FAILURE detected!");
    }

    // For simulation purposes, set user readiness manually
    public void simulateUserDone() {
        if (!status && isOn) {
            userReady = true;
            System.out.println("[Screen] User interaction complete.");
        }
    }
}