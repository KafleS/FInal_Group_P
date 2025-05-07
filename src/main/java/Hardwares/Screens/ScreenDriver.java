package Hardwares.Screens;

import Display.Template;
import Manager.VotingManager;

import java.util.List;

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



    public List<Template> fetchTemplate() {

        return  VotingManager.getLoadedTemplates();
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
