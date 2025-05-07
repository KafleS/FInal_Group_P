package Control;

import Card.*;
import Hardwares.*;
import Hardwares.Battery.BatteryDriver;
import Hardwares.Printer.PrinterDriver;
import Hardwares.SDCards.SDCard;
import Hardwares.SDCards.SDCard1_Driver;
import Hardwares.SDCards.SDCard2_Driver;
import Hardwares.SDCards.SDCard3_Driver;
import Hardwares.latch.LatchDriver;
import Hardwares.Screens.ScreenDriver;
import Manager.VotingManager;

import java.io.PrintWriter;

public class VotingControl {
    private CardHolder cardHolder;
    private Monitor monitor;
    private VotingManager votingManager;
    private ScreenDriver screenDriver;

    public VotingControl(VotingManager votingManager,
                         CardHolder cardHolder,
                         LatchDriver latchDriver,
                         BatteryDriver batteryDriver,
                         PrinterDriver printerDriver,
                         SDCard1_Driver sdCard1,
                         SDCard2_Driver sdCard2,
                         SDCard3_Driver sdCard3,
                         ScreenDriver screenDriver) {
        this.votingManager = votingManager;
        this.cardHolder = cardHolder;
        this.screenDriver = screenDriver;
        this.monitor = new Monitor(
                latchDriver,
                batteryDriver,
                printerDriver,
                sdCard1,
                sdCard2,
                sdCard3,
                screenDriver
        );
        new Thread(this.monitor).start();
    }

    public void notifyCardInserted(String cardData, PrintWriter out) {
        System.out.println("VotingControl received card data: " + cardData);

        if (cardHolder.hasFailure()) {
            out.println("[Screen] Card reader failure. Cannot process card.");
            return;
        }

        cardHolder.insertCard(cardData);
        String readData = cardHolder.readCard();
        out.println("[Screen] Card accepted. Welcome: " + readData);

        screenDriver.turnOn();
        screenDriver.present("okay");
        out.println("[Screen] Ballot Screen Template presented.");

        cardHolder.eraseCard();
        cardHolder.ejectCard();
        out.println("[Screen] Card ejected and erased.");
    }

    public void initializeBallot() {
        votingManager.loadBallot();
        System.out.println("[VotingControl] Ballot loaded successfully.");
    }

    public void registerClient(PrintWriter out) {
        monitor.registerClient(out);
    }

    public void stopMonitor() {
        if (monitor != null) {
            monitor.stopMonitoring();
        }
    }
}
