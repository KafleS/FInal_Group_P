package Process;

import Display.Template;
import Hardwares.Screens.ScreenDriver;

public class VotingProcess {

    private final ScreenDriver screenDriver;

    public VotingProcess(ScreenDriver screenDriver) {
        this.screenDriver = screenDriver;
    }

    public void runTemplate(Template template) {
        if (screenDriver.hasFailure()) {
            System.out.println("[VotingProcess] Screen failure. Skipping template: " + template.getTitle());
            return;
        }

        screenDriver.present(template);

        while (!screenDriver.isExitReady()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Template filled = screenDriver.fetchTemplate();
        System.out.println("[VotingProcess] Captured vote for: " + filled.getTitle());
    }

    public void finish() {
        screenDriver.turnOff();
        System.out.println("[VotingProcess] Voting complete.");
    }
}
