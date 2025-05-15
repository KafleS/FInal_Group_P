package Managers;

import BML.BlankBallot;
import Client.Template;
import Display.TemplateFactory;
import Hardwares.SDCards.SDCard;
import Hardwares.SDCards.SDCard1;
import Hardwares.SDCards.SDCard1_Driver;
import Process.VotingProcess;

import java.util.List;

public class VotingManager implements Runnable {

    private final VotingProcess votingProcess;

    private boolean votingOpen = false;

    public void setVotingOpen(boolean open) {
        this.votingOpen = open;
    }

    public VotingManager(VotingProcess votingProcess) {
        this.votingProcess = votingProcess;
    }

    public void start() {
        new Thread(this).start();
    }

    @Override
    public void run() {

        if (!votingOpen) {
            System.out.println("[VotingManager] Voting is closed. Aborting session.");
            return;
        }

        List<Template> templates = loadTemplates();

        for (Template t : templates) {
            votingProcess.runTemplate(t);
        }

        votingProcess.finish();
    }



    private  List<Template> loadTemplates() {
        try {
            SDCard1 sdCard = new SDCard1(SDCard.Operation.read);
            SDCard1_Driver driver = new SDCard1_Driver(sdCard);
            String json = String.join("\n", driver.read());

            BlankBallot ballot = new BlankBallot(json);
            return TemplateFactory.fromBallot(ballot);
        } catch (Exception e) {
            System.out.println("[VotingManager] Failed to load templates.");
            e.printStackTrace();
            return List.of();
        }
    }
}
