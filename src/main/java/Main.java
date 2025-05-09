// --- Main.java ---
import Card.*;
import Client.SocketHandler;
import Control.VotingControl;
import Hardwares.Battery.Battery;
import Hardwares.Battery.BatteryDriver;
import Hardwares.Printer.Printer;
import Hardwares.Printer.PrinterDriver;
import Hardwares.SDCards.*;
import Hardwares.latch.*;
import Hardwares.Screens.ScreenDriver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import Control.FailureSimulator;
import Managers.VotingManager;
import Managers.AdminManager;
import Process.VotingProcess;

public class Main {
    public static void main(String[] args) {
        // ✅ Hardware setup
        CardReader cardReader = new CardReader();
        CardReaderDriver driver = new CardReaderDriver(cardReader);
        CardHolder holder = new CardHolder(driver);

        Latches latch = new Latches();
        LatchDriver latchDriver = new LatchDriver(latch);

        Battery battery = new Battery();
        BatteryDriver batteryDriver = new BatteryDriver(battery);

        Printer printer = new Printer();
        PrinterDriver printerDriver = new PrinterDriver(printer);

        ScreenDriver screenDriver = ScreenDriver.getInstance();

        SDCard1_Driver sd1 = new SDCard1_Driver(new SDCard1(SDCard.Operation.read));
        SDCard2_Driver sd2 = new SDCard2_Driver(new SDCard2(SDCard.Operation.write));
        SDCard3_Driver sd3 = new SDCard3_Driver(new SDCard3(SDCard.Operation.write));

        VotingProcess votingProcess = new VotingProcess(screenDriver);
        VotingManager votingManager = new VotingManager(votingProcess);
        AdminManager adminManager = new AdminManager();

        VotingControl votingControl = new VotingControl(
                holder,
                latchDriver,
                batteryDriver,
                printerDriver,
                sd1,
                sd2,
                sd3,
                votingManager,
                screenDriver, adminManager
        );


        new Thread(() -> {
            FailureSimulator simulator = new FailureSimulator(printerDriver, latchDriver, sd1, screenDriver);
            simulator.run();
        }).start();


        new Thread(() -> {
            try (ServerSocket failServer = new ServerSocket(12345)) {
                System.out.println("[Main] Waiting for FAILURE channel …");
                Socket failSocket = failServer.accept();
                System.out.println("[Main] FAILURE channel connected");

                PrintWriter failOut = new PrintWriter(failSocket.getOutputStream(), true);
                votingControl.registerClient(failOut);

                // keep thread alive while socket open
                while (!failSocket.isClosed()) {
                    try { Thread.sleep(1_000); } catch (InterruptedException ignore) {}
                }
            } catch (IOException e) {
                System.err.println("[Main] FAILURE channel error: " + e.getMessage());
            }
        }).start();

        try {
            ServerSocket serverSocket = new ServerSocket(9999);
            System.out.println("[Main] Waiting for screen client...");
            Socket screenSocket = serverSocket.accept();
            SocketHandler.initialize(screenSocket);

            System.out.println("[Main] Screen client connected.");

            ObjectOutputStream out = SocketHandler.getInstance().getOutputStream();
            ObjectInputStream in = SocketHandler.getInstance().getInputStream();

            out.flush();
            screenDriver.setOutputStream(out);

            new Thread(() -> {
                while (true) {
                    try {
                        Object obj = in.readObject();
                        if (obj instanceof String msg && msg.startsWith("CRreader:")) {
                            System.out.println("[Main]  Sending card to VotingControl: ");
                            votingControl.notifyCardInserted(msg, out);
                        }
                    } catch (Exception e) {
                        System.err.println("[Main] Lost connection with screen: " + e.getMessage());
                        break;
                    }
                }
            }).start();

        } catch (IOException e) {
            System.err.println("[Main] Failed to open server socket: " + e.getMessage());
        }



        System.out.println("System booted.");
    }
}
