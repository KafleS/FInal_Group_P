package TextEditor;

public class PreambleBuilder {
    private String ballotTitle;
    private String county;
    private String state;
    private String ballotID;
    private String endDate;

    public PreambleBuilder() {}

    public PreambleBuilder(
            String title,
            String county,
            String state,
            String ballotID,
            String endDate
    ) {
        this.ballotTitle = title;
        this.county = county;
        this.state = state;
        this.ballotID = ballotID;
        this.endDate = endDate;
    }



    public String getBallotTitle() { return this.ballotTitle; }

    public String getCounty() { return this.county; }

    public String getState() { return this.state; }

    public String getBallotID() { return this.ballotID; }

    public String getEndDate() { return this.endDate; }


    public void setTitle(String ballotTitle) { this.ballotTitle = ballotTitle; }

    public void setCounty(String county) { this.county = county; }

    public void setState(String state) { this.state = state; }

    public void setID(String ballotID) { this.ballotID = ballotID; }

    public void setEndDate(String endDate) { this.endDate = endDate; }


}
