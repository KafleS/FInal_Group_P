// --- ScreenDriver.java ---
package Hardwares.Screens;

import Client.Template;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class ScreenDriver {
    private static ScreenDriver instance;

    private String receivedMessage = "";
    private boolean screenFailed = false;
    private Template activeTemplate = null;
    private ObjectOutputStream outputStream = null;  // ✅ Output stream for socket communication

    public static ScreenDriver getInstance() {
        if (instance == null) {
            instance = new ScreenDriver();
        }
        return instance;
    }

    private ScreenDriver() {
        // private constructor to enforce singleton
    }

    /**
     * Set the output stream connected to the screen socket.
     * This should be done when the server accepts the screen connection.
     */
    public void setOutputStream(ObjectOutputStream out) {
        this.outputStream = out;
    }

    /**
     * Present the template to the frontend GUI system via socket.
     */
    public void present(Template template) {
        if (screenFailed) {
            System.out.println("[ScreenDriver] Cannot present template. Screen has failed.");
            return;
        }

        this.activeTemplate = template;
        this.receivedMessage = "[Template Ready: " + template.getTitle() + "]";
        System.out.println("[ScreenDriver] Template prepared for GUI: " + template.getTitle());

        // ✅ Send the template to the screen via existing socket output stream
        if (outputStream != null) {
            try {
                synchronized (outputStream) {
                    outputStream.writeObject(template);
                    outputStream.flush();
                    System.out.println("[ScreenDriver] ✅ Template sent to screen via shared socket.");
                }
            } catch (IOException e) {
                System.err.println("[ScreenDriver] ❌ Failed to send template: " + e.getMessage());
            }
        } else {
            System.err.println("[ScreenDriver] ❌ Output stream to screen not set.");
        }
    }

    /**
     * Accept backend messages to simulate screen status or feedback.
     */
    public void readExternalMessage(String input) {
        if (input == null) return;

        input = input.trim().toLowerCase();

        if (input.startsWith("scfail")) {
            screenFailed = true;
            receivedMessage = "FAILURE: Screen has failed!";
        } else if (input.startsWith("scd")) {
            receivedMessage = input.substring(3).trim(); // Remove "scd" prefix
        } else {
            receivedMessage = "Unknown message: " + input;
        }

        System.out.println("[ScreenDriver] Received: " + receivedMessage);
    }

    public String getLastMessage() {
        return receivedMessage;
    }

    public boolean hasFailure() {
        return screenFailed;
    }

    public void resetFailure() {
        screenFailed = false;
    }

    public Template getActiveTemplate() {
        return activeTemplate;
    }
}
