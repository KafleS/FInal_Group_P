package Managers;

public class AdminManager {
    private boolean votingOpen;

    public AdminManager() {
        this.votingOpen = false; // voting is closed by default
    }

    /**
     * Opens the voting session.
     */
    public void openVotingSession() {
        if (!votingOpen) {
            votingOpen = true;
            System.out.println("[AdminManager]  Voting session is now OPEN.");
        } else {
            System.out.println("[AdminManager]  Voting session was already open.");
        }
    }

    /**
     * Closes the voting session.
     */
    public void closeVotingSession() {
        if (votingOpen) {
            votingOpen = false;
            System.out.println("[AdminManager] Voting session is now CLOSED.");
        } else {
            System.out.println("[AdminManager] Voting session was already closed.");
        }
    }

    /**
     * Returns whether the voting session is currently open.
     * @return true if open, false if closed
     */
    public boolean isVotingOpen() {
        return votingOpen;
    }
}
