package Hardwares.Printer;

public class PrinterDriver {
    private final Printer printer;

    public PrinterDriver(Printer printer) {
        this.printer = printer;
    }

    /** Simple check for failure */
    public boolean hasFailed() {
        return printer.failure();
    }

    /**
     * Attempt to print.  If the printer is in a failed state, print error for now.
     */
    public void print(String text) {
        if (printer.failure()) {
            System.out.println("Printer is currently unavailable");
        }
        printer.print(text);
    }

    /** For tests or simulation: manually flip the failure flag. */
    public void setFailure(boolean status) {
        printer.setFailure(status);
    }
}
