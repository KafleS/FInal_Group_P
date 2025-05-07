// --- Main.java ---
import Card.*;
import Control.VotingControl;
import Hardwares.Battery.Battery;
import Hardwares.Battery.BatteryDriver;
import Hardwares.Printer.Printer;
import Hardwares.Printer.PrinterDriver;
import Hardwares.SDCards.*;
import Hardwares.latch.*;
import Hardwares.Screens.ScreenDriver;

import Control.FailureSimulator;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import Managers.VotingManager;
import Process.VotingProcess;

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

        // ScreenDriver (singleton)
        ScreenDriver screenDriver = ScreenDriver.getInstance();

        // SD cards
        SDCard1_Driver sd1 = new SDCard1_Driver(new SDCard1(SDCard.Operation.read));
        SDCard2_Driver sd2 = new SDCard2_Driver(new SDCard2(SDCard.Operation.write));
        SDCard3_Driver sd3 = new SDCard3_Driver(new SDCard3(SDCard.Operation.write));

        // Voting system logic
        VotingProcess votingProcess = new VotingProcess(screenDriver);
        VotingManager votingManager = new VotingManager(votingProcess);

        VotingControl votingControl = new VotingControl(
                holder,
                latchDriver,
                batteryDriver,
                printerDriver,
                sd1,
                sd2,
                sd3,
                votingManager,
                screenDriver
        );

        // Start card reader socket server
        new Thread(() -> runCardReaderServer(votingControl)).start();

        // Start failure simulator
        FailureSimulator simulator = new FailureSimulator(printerDriver, latchDriver, sd1, cardReader, screenDriver);
        new Thread(simulator).start();

        System.out.println("System booted. Waiting for card input...");
    }

    /**
     * Server that listens for card reader input and shares its socket's output stream with ScreenDriver.
     */
    public static void runCardReaderServer(VotingControl votingControl) {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Card Reader Server started at port 12345");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("[Main] Screen connected: " + clientSocket.getInetAddress());

                try {
                    // Setup streams
                    ObjectOutputStream objectOut = new ObjectOutputStream(clientSocket.getOutputStream());
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);

                    // Share output stream with ScreenDriver
                    ScreenDriver.getInstance().setOutputStream(objectOut);

                    // Handle incoming card data
                    String cardData = in.readLine();
                    System.out.println("[Main] Received card input: " + cardData);

                    // Route to voting logic
                    votingControl.notifyCardInserted(cardData, out);

                } catch (Exception e) {
                    System.err.println("[Main] Error handling client socket: " + e.getMessage());
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            System.err.println("[Main] Server socket failure: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
