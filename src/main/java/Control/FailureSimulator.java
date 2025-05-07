
package Control;

import Hardwares.Printer.PrinterDriver;
import Hardwares.latch.LatchDriver;
import Hardwares.SDCards.SDCard1_Driver;
import Hardwares.Screens.ScreenDriver;

import java.util.Scanner;

public class FailureSimulator implements Runnable {
    private final PrinterDriver printer;
    private final LatchDriver latch;
    private final SDCard1_Driver sd1;
    private final ScreenDriver screen;

    public FailureSimulator(PrinterDriver printer,
                            LatchDriver latch,
                            SDCard1_Driver sd1,
                            ScreenDriver screen) {
        this.printer = printer;
        this.latch = latch;
        this.sd1 = sd1;
        this.screen = screen;
    }

    @Override
    public void run() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("Press 'f' to trigger failure mode");
            if (!"f".equalsIgnoreCase(sc.nextLine().trim())) continue;
            System.out.println("Enter code: 1=printer, 2=latch, 3=sd1, 4=screen");
            switch (sc.nextLine().trim()) {
                case "1":
                    printer.setFailure(true);
                    System.out.println("[Sim] Printer failed");
                    break;
                case "2": /*latch.setFailure(true);*/
                    System.out.println("[Sim] Latch failed");
                    break;
                case "3": /*sd1.setFailure(true);*/
                    System.out.println("[Sim] SD1 failed");
                    break;
                case "4":
                    screen.setFailure(true);
                    screen.turnOff();
                    System.out.println("[Sim] Screen off");
                    break;
                default:
                    System.out.println("[Sim] Invalid code");
            }
        }
    }
}