package Hardwares.Screens;

import Client.Screen;
import Client.SocketHandler;
import Client.Template;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ScreenDriver {

    private static ScreenDriver instance;
    private Screen screen;
    private String receivedMessage = "";
    private boolean screenFailed = false;
    private Template activeTemplate = null;
    private ObjectOutputStream outputStream = null;
    private ObjectOutputStream out;

    public static ScreenDriver getInstance() {
        if (instance == null) {
            instance = new ScreenDriver();
        }
        return instance;
    }

    public ScreenDriver() {
         // private constructor to enforce singleton

    }
    public void setScreen(Screen screen) {
        this.screen = screen;
    }
    public void setOutputStream(ObjectOutputStream out) {
        this.outputStream = out;
    }

    public void present(Template template) {
        if (screenFailed) {
            System.out.println("[ScreenDriver]  Cannot present template. Screen has failed.");
            return;
        }

        this.activeTemplate = template;
        this.receivedMessage = "[Template Ready: " + template.getTitle() + "]";
        System.out.println("[ScreenDriver] Preparing to send template:");
        System.out.println("[ScreenDriver]  Title: " + template.getTitle());
        System.out.println("[ScreenDriver]   Questions: " + template.getQuestionData());

        if (outputStream != null) {
            try {
                synchronized (outputStream) {
                    System.out.println("[ScreenDriver]  Sending template over socket...");
                    outputStream.writeObject(template);
                    outputStream.flush();
                    System.out.println("[ScreenDriver]  Template sent to screen via shared socket.");
                }
            } catch (IOException e) {
                System.err.println("[ScreenDriver]  Failed to send template: " + e.getMessage());
            }
        } else {
            System.err.println("[ScreenDriver]  Output stream to screen not set.");
        }
    }

    public Template waitForTemplateResponse() {
        try {
            ObjectOutputStream out = SocketHandler.getInstance().getOutputStream();
            ObjectInputStream in = SocketHandler.getInstance().getInputStream();

            System.out.println("[ScreenDriver]  Waiting for user to finish interaction on screen...");

            while (true) {
                Thread.sleep(300);
                System.out.println("[ScreenDriver]  Polling 'isready' from screen...");

                synchronized (out) {
                    out.writeObject("isready");
                    out.flush();
                }

                Object readyResponse = in.readObject();
                if (readyResponse instanceof Boolean ready) {
                    System.out.println("[ScreenDriver]  isReady response: " + ready);

                    if (ready) {
                        System.out.println("[ScreenDriver]  Requesting modified template...");
                        synchronized (out) {
                            out.writeObject("gettemplate");
                            out.flush();
                        }

                        Object response = in.readObject();
                        if (response instanceof Template t) {
                            System.out.println("[ScreenDriver]  Received filled template from screen: " + t.getTitle());
                            return t;
                        } else {
                            System.err.println("[ScreenDriver]  Unexpected response type: " + response.getClass());
                        }
                    }
                } else {
                    System.err.println("[ScreenDriver] Invalid isReady response: " + readyResponse);
                }
            }
        } catch (Exception e) {
            System.err.println("[ScreenDriver]  Error in waitForTemplateResponse: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public void setFailure(boolean status) {
        screenFailed = status;
        sendTag(status ? "SCREEN_FAILURE" : "SCREEN_OK");
        if (screen != null) screen.setFailure(status);
    }

    //new code
    public void turnOff() {
        screenFailed = true;
        sendTag("TURN_OFF");
        if (screen != null) screen.screenOff();
    }


    //new code
    private void sendTag(String tag) {
        if (out == null) return;
        try {
            synchronized (out) {
                out.writeObject(tag);
                out.flush();
            }
        } catch (IOException e) {
            System.err.println("[ScreenDriver] Could not send tag '" + tag + "': " + e.getMessage());
        }
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
