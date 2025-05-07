package Process;

import Client.Template;
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

        System.out.println("[VotingProcess] Presenting template: " + template.getTitle());
        screenDriver.present(template);

        // Wait up to 1 minute for the user to complete interaction
        int secondsWaited = 0;
        while (!screenDriver.getLastMessage().equalsIgnoreCase("done") && secondsWaited < 5) {
            try {
                Thread.sleep(500); // wait 1 second at a time
                secondsWaited++;
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }

        if (screenDriver.getLastMessage().equalsIgnoreCase("done")) {
            System.out.println("[VotingProcess] User completed vote for: " + template.getTitle());
        } else {
            System.out.println("[VotingProcess] Timeout reached. Proceeding to next template.");
        }
    }

    public void finish() {
        System.out.println("[VotingProcess] Voting complete. Turning off screen.");
        // Optional: add screenDriver.turnOff() or similar logic
    }
}
