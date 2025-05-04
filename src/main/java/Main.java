// --- Main.java ---
import Card.*;
import Control.VotingControl;
import Hardwares.Battery.Battery;
import Hardwares.Battery.BatteryDriver;
import Hardwares.Printer.Printer;
import Hardwares.Printer.PrinterDriver;
import Hardwares.SDCards.*;
import Hardwares.latch.*;
import Control.FailureSimulator;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        // Card system setup
        CardReader cardReader = new CardReader();
        CardReaderDriver driver = new CardReaderDriver(cardReader);
        CardHolder holder = new CardHolder(driver);

        // Hardware setup
        Latches latch = new Latches();
        LatchDriver latchDriver = new LatchDriver(latch);

        Battery battery = new Battery();
        BatteryDriver batteryDriver = new BatteryDriver(battery);

        Printer printer = new Printer();
        PrinterDriver printerDriver = new PrinterDriver(printer);

        SDCard1_Driver sd1 = new SDCard1_Driver(new SDCard1(SDCard.Operation.read));
        SDCard2_Driver sd2 = new SDCard2_Driver(new SDCard2(SDCard.Operation.write));
        SDCard3_Driver sd3 = new SDCard3_Driver(new SDCard3(SDCard.Operation.write ));

        // Voting control setup
        VotingControl votingControl = new VotingControl(
                holder,
                latchDriver,
                batteryDriver,
                printerDriver,
                sd1,
                sd2,
                sd3
        );

        new Thread(() -> runCardReaderServer(votingControl)).start();

        // Start terminal failure simulator
        FailureSimulator simulator = new FailureSimulator(printerDriver, latchDriver, sd1, cardReader);
        new Thread(simulator).start();

        System.out.println("System booted. Waiting for card input...");
    }

    public static void runCardReaderServer(VotingControl votingControl) {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Card Reader Server started at port 12345");
            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String cardData = in.readLine();
                    votingControl.notifyCardInserted(cardData);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
