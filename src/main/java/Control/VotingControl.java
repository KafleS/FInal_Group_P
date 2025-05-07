package Control;

import Card.*;
import Hardwares.Monitor;
import Managers.VotingManager;
import Hardwares.Battery.BatteryDriver;
import Hardwares.Printer.PrinterDriver;
import Hardwares.SDCards.SDCard2_Driver;
import Hardwares.SDCards.SDCard3_Driver;
import Hardwares.SDCards.SDCard1_Driver;
import Hardwares.Screens.ScreenDriver;
import Hardwares.latch.LatchDriver;

import java.io.PrintWriter;
import java.sql.SQLOutput;

public class VotingControl {
    private final CardHolder cardHolder;
    private final Monitor monitor;
    private final VotingManager votingManager;

    public VotingControl(
            CardHolder cardHolder,
            LatchDriver latchDriver,
            BatteryDriver batteryDriver,
            PrinterDriver printerDriver,
            SDCard1_Driver sdCard1,
            SDCard2_Driver sdCard2,
            SDCard3_Driver sdCard3,
            VotingManager votingManager,
            ScreenDriver screenDriver
    ) {
        this.cardHolder = cardHolder;
        this.votingManager = votingManager;

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

        CardType type = cardHolder.getCardType();

        System.out.println("[VotingControl] CardType resolved to: " + type);
        switch (type) {
            case VOTER -> {
                String readData = cardHolder.readCard();
                System.out.println(" in ma in voting control"+ readData);
                out.println("[Screen] Voter card accepted. Welcome: " + readData);
                votingManager.start();
            }
            case OFFICIAL -> {
                out.println("[Screen] Admin card detected. Access denied to voting screen.");
                //cardHolder.ejectCard();
            }
            case UNKNOWN -> {
                out.println("[Screen] Unknown or invalid card. Please try again.");
               // cardHolder.ejectCard();
            }
        }
    }


    public void stopMonitor() {
        if (monitor != null) {
            monitor.stopMonitoring();
        }
    }
}
