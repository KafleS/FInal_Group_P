package Display;

import Hardwares.SDCards.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.Collection;

public class VoteRecording {
    private final SDCard2_Driver driver2;
    private final SDCard3_Driver driver3;
    private final ObjectMapper mapper = new ObjectMapper();

    public VoteRecording() {
        // Initialize writers (for storing final vote data)
        this.driver2 = new SDCard2_Driver(new SDCard2(SDCard.Operation.overwrite));
        this.driver3 = new SDCard3_Driver(new SDCard3(SDCard.Operation.overwrite));
    }

    public void recordVotes(Collection<VoterPage.VoteData> votes) throws IOException {
        ArrayNode root2 = mapper.createArrayNode();
        ArrayNode root3 = mapper.createArrayNode();

        // Read using separate readers with Operation.read
        SDCard2_Driver reader2 = new SDCard2_Driver(new SDCard2(SDCard.Operation.read));
        SDCard3_Driver reader3 = new SDCard3_Driver(new SDCard3(SDCard.Operation.read));

        String existing2 = String.join("\n", reader2.read());
        if (!existing2.isBlank()) {
            JsonNode json = mapper.readTree(existing2);
            if (json.isArray()) root2 = (ArrayNode) json;
        }

        String existing3 = String.join("\n", reader3.read());
        if (!existing3.isBlank()) {
            JsonNode json = mapper.readTree(existing3);
            if (json.isArray()) root3 = (ArrayNode) json;
        }

        // Add or replace votes per ID
        for (VoterPage.VoteData voteData : votes) {
            // Remove duplicates in both roots
            for (int i = root2.size() - 1; i >= 0; i--) {
                if (root2.get(i).get("id").asText().equals(voteData.id)) {
                    root2.remove(i);
                }
            }

            for (int i = root3.size() - 1; i >= 0; i--) {
                if (root3.get(i).get("id").asText().equals(voteData.id)) {
                    root3.remove(i);
                }
            }

            // Add the vote to both roots
            ObjectNode vote = mapper.createObjectNode();
            vote.put("id", voteData.id);
            vote.put("title", voteData.title);
            vote.put("description", voteData.description);
            vote.put("selectedOption", voteData.option);

            root2.add(vote);
            root3.add(vote.deepCopy());
        }

        // Save back to voter1 and voter2
        driver2.overwrite(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root2));
        driver3.overwrite(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root3));

        System.out.println("Votes successfully recorded to both voter1 and voter2.");
    }
}