//package Hardwares;
//
//import Hardwares.latch.LatchDriver;
//import Hardwares.Battery.BatteryDriver;
//import Hardwares.Printer.PrinterDriver;
//import Hardwares.SDCards.SDCard1_Driver;
//import Hardwares.SDCards.SDCard2_Driver;
//import Hardwares.SDCards.SDCard3_Driver;
//import Hardwares.Screens.ScreenDriver;
//
//
//public class Monitor implements Runnable {
//    private final LatchDriver latchDriver;
//    private final BatteryDriver batteryDriver;
//    private final PrinterDriver printerDriver;
//    private final SDCard1_Driver sdCard1;
//    private final SDCard2_Driver sdCard2;
//    private final SDCard3_Driver sdCard3;
//    private final ScreenDriver screenDriver;
//
//
//
//    private volatile boolean running = true;
//
//    public Monitor(LatchDriver latchDriver,
//                   BatteryDriver batteryDriver,
//                   PrinterDriver printerDriver,
//                   SDCard1_Driver sdCard1,
//                   SDCard2_Driver sdCard2,
//                   SDCard3_Driver sdCard3,
//                   ScreenDriver screenDriver) {
//        this.latchDriver = latchDriver;
//        this.batteryDriver = batteryDriver;
//        this.printerDriver = printerDriver;
//        this.sdCard1 = sdCard1;
//        this.sdCard2 = sdCard2;
//        this.sdCard3 = sdCard3;
//        this.screenDriver = screenDriver;
//    }
//
//    public void stopMonitoring() {
//        running = false;
//    }
//
//    @Override
//    public void run() {
//        while (running) {
//            System.out.println("[Monitor] Checking hardware status...");
//
//            if (latchDriver.hasFailure()) {
//                System.out.println("[Monitor] Latch failure detected!");
//
//            }
//            if (batteryDriver.hasFailed()) {
//                System.out.println("[Monitor] Battery failure detected!");
//
//            }
//            if (printerDriver.hasFailed()) {
//                System.out.println("[Monitor] Printer failure detected!");
//
//            }
//            if (sdCard1.hasFailure()) {
//                System.out.println("[Monitor] SDCard1 failure detected!");
//
//            }
//            if (sdCard2.hasFailure()) {
//                System.out.println("[Monitor] SDCard2 failure detected!");
//
//            }
//            if (sdCard3.hasFailure()) {
//                System.out.println("[Monitor] SDCard3 failure detected!");
//
//            }
//            if (screenDriver.hasFailure()) {
//                System.out.println("[Monitor] Screen failure detected!");
//
//            }
//
//            try {
//                Thread.sleep(5000); // check every 5 seconds
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
//        }
//    }
//}


package Hardwares;

import Hardwares.latch.LatchDriver;
import Hardwares.Battery.BatteryDriver;
import Hardwares.Printer.PrinterDriver;
import Hardwares.SDCards.SDCard1_Driver;
import Hardwares.SDCards.SDCard2_Driver;
import Hardwares.SDCards.SDCard3_Driver;
import Hardwares.Screens.ScreenDriver;

import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;


public class Monitor implements Runnable {
    private final LatchDriver latchDriver;
    private final BatteryDriver batteryDriver;
    private final PrinterDriver printerDriver;
    private final SDCard1_Driver sdCard1;
    private final SDCard2_Driver sdCard2;
    private final SDCard3_Driver sdCard3;
    private final ScreenDriver screenDriver;



    private volatile boolean running = true;

    private final List<PrintWriter> clients = new CopyOnWriteArrayList<>();


    public Monitor(LatchDriver latchDriver,
                   BatteryDriver batteryDriver,
                   PrinterDriver printerDriver,
                   SDCard1_Driver sdCard1,
                   SDCard2_Driver sdCard2,
                   SDCard3_Driver sdCard3,
                   ScreenDriver screenDriver) {
        this.latchDriver = latchDriver;
        this.batteryDriver = batteryDriver;
        this.printerDriver = printerDriver;
        this.sdCard1 = sdCard1;
        this.sdCard2 = sdCard2;
        this.sdCard3 = sdCard3;
        this.screenDriver = screenDriver;
    }

    public void registerClient(PrintWriter out) {
        clients.add(out);
    }

    public void stopMonitoring() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            System.out.println("[Monitor] Checking hardware status...");
            if ( latchDriver.hasFailure() || batteryDriver.hasFailed() || printerDriver.hasFailed()
                    || sdCard1.hasFailure()    || sdCard2.hasFailure()   || sdCard3.hasFailure()
                    || screenDriver.hasFailure() ) {
                screenDriver.turnOff();
                broadcastFailure("FAILURE");
                break;
            }
            try { TimeUnit.SECONDS.sleep(5); } catch (InterruptedException ignore) {}
        }
    }

    private void broadcastFailure(String tag) {
        for (PrintWriter out : clients) {
            out.println(tag);
        }
    }
}

