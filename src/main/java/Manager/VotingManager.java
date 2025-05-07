package Manager;

import BML.BlankBallot;
import Display.Template;
import Display.TemplateFactory;
import Hardwares.SDCards.SDCard;
import Hardwares.SDCards.SDCard1;
import Hardwares.SDCards.SDCard1_Driver;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

public class VotingManager implements Runnable {

    private static BlankBallot loadedBlankBallot;
    private static List<Template> loadedTemplates;

    @Override
    public void run() {
        loadBallot();
    }

    /**
     * Loads the Ballot and template from the SD Card.
     */
    public void loadBallot() {
        try {
            SDCard1 sdCard1 = new SDCard1(SDCard.Operation.read);
            SDCard1_Driver sdCard1Driver = new SDCard1_Driver(sdCard1);

            String json = String.join("\n", sdCard1Driver.read());
            System.out.println("[SUCCESS] JSON read from SD Card 1:");

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);

            if (!root.has("propositions")) {
                System.out.println("[ERROR] 'propositions' missing in JSON!");
            } else {
                System.out.println("[INFO] Found propositions, count: " + root.get("propositions").size());
            }

            loadedBlankBallot = new BlankBallot(json);
            loadedTemplates = TemplateFactory.fromBallot(loadedBlankBallot);

            for (Template t : loadedTemplates) {
                System.out.println("\nTemplate ID: " + t.getId());
                System.out.println("Title: " + t.getTitle());
                System.out.println("Description: " + t.getDescription());
                System.out.println("Instructions: " + t.getInstructions());
                System.out.println("Options: " + Arrays.toString(t.getQuestionData().getOptions()));
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[ERROR] VotingManager failed to load ballot or templates.");
        }
    }

    public static BlankBallot getLoadedBallot() {
        return loadedBlankBallot;
    }

    public static List<Template> getLoadedTemplates() {
        if (loadedTemplates == null) {
            new VotingManager().loadBallot();
        }
        System.out.println("loaded templates2: " + loadedTemplates + " ");
        return loadedTemplates;
    }
}