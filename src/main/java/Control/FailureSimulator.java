package Control;

import java.util.Scanner;
import Card.*;
import Hardwares.Printer.PrinterDriver;
import Hardwares.latch.LatchDriver;
import Hardwares.SDCards.SDCard1_Driver;

public class FailureSimulator implements Runnable {
    private final PrinterDriver printerDriver;
    private final LatchDriver latchDriver;
    private final SDCard1_Driver sdCard1Driver;
    private final CardReader cardReader;

    public FailureSimulator(PrinterDriver printerDriver, LatchDriver latchDriver, SDCard1_Driver sdCard1Driver, CardReader cardReader) {
        this.printerDriver = printerDriver;
        this.latchDriver = latchDriver;
        this.sdCard1Driver = sdCard1Driver;
        this.cardReader = cardReader;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print(">> ");
            String input = scanner.nextLine().trim().toUpperCase();

            switch (input) {
                case "F":
                    System.out.println("Type 1 to fail printer");
                    System.out.println("Type 2 to fail latch");
                    System.out.println("Type 3 to fail SDCard1");
                    String failType = scanner.nextLine().trim();
                    handleFailure(failType);
                    break;

                case "V":
                    System.out.println("Enter card ID to insert and erase:");
                    String cardId = scanner.nextLine().trim();
                    cardReader.insertCard(cardId);
                    System.out.println(cardId);
                    cardReader.eraseCard();
                    cardReader.ejectCard();
                    System.out.println(cardId + " card ejected and erased");
                    break;

                case "A":
                    System.out.println("Enter card ID to insert and eject only:");
                    String cardIdA = scanner.nextLine().trim();
                    cardReader.insertCard(cardIdA);
                    System.out.println(cardIdA);
                    cardReader.ejectCard();
                    System.out.println(cardIdA + " card ejected");
                    break;

                default:
                    System.out.println("Unknown command. Use F, V, or A.");
            }
        }
    }

    private void handleFailure(String type) {
        switch (type) {
            case "1":
                printerDriver.setFailure(true);
                System.out.println("Printer failure recorded");
                break;
            case "2":
                latchDriver.openDoor(); // simulate latch fault
                System.out.println("Latch failure simulated");
                break;
            case "3":
                // Simulated via card logic — you'd need a `setFailure` on the SDCard
                System.out.println("SDCard1 failure not implemented (needs support)");
                break;
            default:
                System.out.println("Invalid failure type");
        }
    }
}
