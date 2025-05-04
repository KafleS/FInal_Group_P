package BML;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Preamble {
    private final String ballotTitle;
    private final String county;
    private final String state;
    private final String ballotID;
    private final String endDate;

    @JsonCreator
    public Preamble(
            @JsonProperty("ballotTitle") String ballotTitle,
            @JsonProperty("county") String county,
            @JsonProperty("state") String state,
            @JsonProperty("ballotID") String ballotID,
            @JsonProperty("endDate") String endDate
    ) {
        this.ballotTitle = ballotTitle;
        this.county = county;
        this.state = state;
        this.ballotID = ballotID;
        this.endDate = endDate;
    }

    public String getBallotTitle() {
        return this.ballotTitle;
    }

    public String getCounty() {
        return this.county;
    }

    public String getState() {
        return this.state;
    }

    public String getBallotID() {
        return this.ballotID;
    }

    public String getEndDate() {
        return this.endDate;
    }

    public String toString() {
        return "Preamble { ballotTitle = " + ballotTitle + ", " +
                "county = " + county + ", " +
                "state = " + state + ", " +
                "ballotID = " + ballotID + ", " +
                "endDate = " + endDate + '}';
    }
}
