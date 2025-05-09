package Hardwares.SDCards;

import Client.VotingMachinePage;

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


    /**
     * Records the votes in the voter1.txt and voter2.txt using the SDCARD1 and SDCARD2
     * @param votes the voter data
     * @throws IOException expection
     */
    public void recordVotes(Collection<VotingMachinePage.VoteData> votes) throws IOException {
        // Use overwrite mode since we'll manage the array ourselves
//        SDCard2_Driver driver2 = new SDCard2_Driver(new SDCard2(SDCard.Operation.overwrite));
//        SDCard3_Driver driver3 = new SDCard3_Driver(new SDCard3(SDCard.Operation.overwrite));

        // Read existing content as array
        ArrayNode existingVotes2 = mapper.createArrayNode();
        ArrayNode existingVotes3 = mapper.createArrayNode();

        SDCard2_Driver reader2 = new SDCard2_Driver(new SDCard2(SDCard.Operation.read));
        SDCard3_Driver reader3 = new SDCard3_Driver(new SDCard3(SDCard.Operation.read));

        String content2 = String.join("\n", reader2.read());
        if (!content2.isBlank()) {
            JsonNode node = mapper.readTree(content2);
            if (node.isArray()) {
                existingVotes2 = (ArrayNode) node;
            }
        }

        String content3 = String.join("\n", reader3.read());
        if (!content3.isBlank()) {
            JsonNode node = mapper.readTree(content3);
            if (node.isArray()) {
                existingVotes3 = (ArrayNode) node;
            }
        }

        // Append new votes
        for (VotingMachinePage.VoteData voteData : votes) {
            ObjectNode vote = mapper.createObjectNode();
            vote.put("id", voteData.id);
            vote.put("title", voteData.title);
            vote.put("description", voteData.description);
            vote.put("selectedOption", voteData.option);

            existingVotes2.add(vote);
            existingVotes3.add(vote.deepCopy());
        }

        // Overwrite with the updated arrays
        driver2.overwrite(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(existingVotes2));
        driver3.overwrite(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(existingVotes3));

        System.out.println("Votes successfully appended as JSON array.");
    }
}