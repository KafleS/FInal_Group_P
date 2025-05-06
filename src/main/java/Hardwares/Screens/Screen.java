// --- Screen.java ---
package Hardwares.Screens;

import Display.Template;

public class Screen {
    private boolean isOn = false;
    private boolean status = false;
    private Template currentTemplate = null;
    private boolean userReady = false;

    public void screenOn() {
        if (!status) {
            isOn = true;
            System.out.println("[Screen] Powered ON.");
        } else {
            System.out.println("[Screen] Cannot power on: screen failure detected.");
        }
    }

    public void presentTemplate(Template template) {
        if (!status && isOn) {
            currentTemplate = template;
            userReady = false;
            System.out.println("[Screen] Template loaded:");
            System.out.println("  Title: " + template.getTitle());
            System.out.println("  Instructions: " + template.getInstructions());
            System.out.println("  Options: " + String.join(", ", template.getQuestionData().getOptions()));
        } else {
            System.out.println("[Screen] Cannot present template. Screen is either off or failed.");
        }
    }


    public boolean exitReady() {
        return userReady;
    }

    public Template returnTemplate() {
        return (!status && currentTemplate != null) ? currentTemplate : null;
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

    public void simulateUserDone() {
        if (!status && isOn) {
            userReady = true;
            System.out.println("[Screen] User interaction complete.");
        }
    }
}
