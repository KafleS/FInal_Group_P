// --- Main.java ---
import Card.*;
import Control.VotingControl;
import Hardwares.Battery.Battery;
import Hardwares.Battery.BatteryDriver;
import Hardwares.Printer.Printer;
import Hardwares.Printer.PrinterDriver;
import Hardwares.SDCards.*;
import Hardwares.latch.*;
import Hardwares.Screens.*;

import Control.FailureSimulator;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import Managers.VotingManager;
import Process.VotingProcess;




// --- Main.java ---
import Card.*;
import Control.VotingControl;
import Control.FailureSimulator;
import Display.Template;
import Hardwares.Battery.Battery;
import Hardwares.Battery.BatteryDriver;
import Hardwares.Printer.Printer;
import Hardwares.Printer.PrinterDriver;
import Hardwares.SDCards.*;
import Hardwares.latch.*;
import Hardwares.Screens.*;
import Managers.VotingManager;
import Process.VotingProcess;

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

        Screen screen = new Screen();
        ScreenDriver screenDriver = new ScreenDriver(screen);

        SDCard1_Driver sd1 = new SDCard1_Driver(new SDCard1(SDCard.Operation.read));
        SDCard2_Driver sd2 = new SDCard2_Driver(new SDCard2(SDCard.Operation.write));
        SDCard3_Driver sd3 = new SDCard3_Driver(new SDCard3(SDCard.Operation.write));

        // Create VotingProcess and VotingManager
        VotingProcess votingProcess = new VotingProcess(screenDriver);
        VotingManager votingManager = new VotingManager(votingProcess);

        // Create VotingControl (screenDriver passed only for Monitor)
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

        // Launch CardReader server
        new Thread(() -> runCardReaderServer(votingControl)).start();

        // Start Failure Simulator
        FailureSimulator simulator = new FailureSimulator(printerDriver, latchDriver, sd1, cardReader, screenDriver);
        new Thread(simulator).start();

        System.out.println("System booted. Waiting for card input...");
    }

    public static void runCardReaderServer(VotingControl votingControl) {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Card Reader Server started at port 12345");
            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
                    String cardData = in.readLine();
                    votingControl.notifyCardInserted(cardData, out);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}





//
//        try {
//            List<String> ballotLines = sd1.read();
//            String json = ballotLines.stream().collect(Collectors.joining("\n"));
//
//            BlankBallot ballot = new BlankBallot(json);
//            List<Template> templates = TemplateFactory.fromBallot(ballot);
//
//            screen.screenOn();
//            if (!templates.isEmpty()) {
//                screen.presentTemplate(templates.get(0)); // Display the first proposition
//            } else {
//                System.out.println("[Main] No templates were created.");
//            }
//        } catch (Exception e) {
//            System.out.println("[Main] Error loading or displaying template:");
//            e.printStackTrace();
//        }
