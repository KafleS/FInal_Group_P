// --- VotingControl.java ---
package Control;

import Card.*;
import Hardwares.*;
import Hardwares.Battery.BatteryDriver;
import Hardwares.Printer.PrinterDriver;
import Hardwares.SDCards.SDCard1_Driver;
import Hardwares.SDCards.SDCard2_Driver;
import Hardwares.SDCards.SDCard3_Driver;
import Hardwares.latch.LatchDriver;
import Manager.VotingManager;

public class VotingControl {
    private CardHolder cardHolder;
    private Monitor monitor;
    private VotingManager votingManager;

    public VotingControl(VotingManager votingManager,
            CardHolder cardHolder,
                         LatchDriver latchDriver,
                         BatteryDriver batteryDriver,
                         PrinterDriver printerDriver,
                         SDCard1_Driver sdCard1,
                         SDCard2_Driver sdCard2,
                         SDCard3_Driver sdCard3) {
        this.votingManager = votingManager;
        this.cardHolder = cardHolder;
        this.monitor = new Monitor(latchDriver, batteryDriver, printerDriver, sdCard1, sdCard2, sdCard3);
        new Thread(this.monitor).start();
    }

    public void notifyCardInserted(String cardData) {
        System.out.println("VotingControl received card data: " + cardData);

        if (cardHolder.hasFailure()) {
            System.out.println("Error: Card reader failure. Cannot process card.");
            return;
        }

        cardHolder.insertCard(cardData);
        String readData = cardHolder.readCard();
        System.out.println("Read from card: " + readData);


        cardHolder.eraseCard();
        cardHolder.ejectCard();
    }

    //Initializes the Ballot and templates
    public void initializeBallot() {
        System.out.println("[VotingControl] Loading the ballot");
        votingManager.loadBallot();
        System.out.println("[VotingControl] Ballot loaded successfully.");
    }

    public void stopMonitor() {
        if (monitor != null) {
            monitor.stopMonitoring();
        }
    }
}
