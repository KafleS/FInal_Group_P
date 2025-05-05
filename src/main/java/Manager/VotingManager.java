package Manager;

import BML.BlankBallot;
import Display.Template;
import Display.TemplateFactory;
import Hardwares.SDCards.SDCard;
import Hardwares.SDCards.SDCard1;
import Hardwares.SDCards.SDCard1_Driver;

import java.util.List;

public class VotingManager {

    private static BlankBallot loadedBlankBallot;
    private static List<Template> loadedTemplates;



    /**
     * Loads the Ballot and template from the SD Card.
     */
    public void loadBallot() {
        try {

            //  SD Card 1 (slot 0 for ballot.txt)
            SDCard1 sdCard1 = new SDCard1(SDCard.Operation.read);
            SDCard1_Driver sdCard1Driver = new SDCard1_Driver(sdCard1);

            // Read JSON from ballot.txt via SD cardl
            String json = String.join("\n", sdCard1Driver.read());
            System.out.println("[SUCCESS] JSON read from SD Card 1:");
            System.out.println(json);

            //  Convert JSON into Ballot
            loadedBlankBallot = new BlankBallot(json);

            //  Generate Templates from Ballot
            loadedTemplates = TemplateFactory.fromBallot(loadedBlankBallot);


            System.out.println("loaded templates: " + loadedTemplates);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[ERROR] VotingManager failed to load ballot or templates.");
        }
    }

    public static BlankBallot getLoadedBallot() {
        return loadedBlankBallot;
    }

    public static List<Template> getLoadedTemplates() {
        return loadedTemplates;
    }
}