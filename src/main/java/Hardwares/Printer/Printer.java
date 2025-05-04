package Hardwares.Printer;

public class Printer {
    private boolean status = false;
    public Printer() {}

    public void print(String text) {
        if (text == null || text.isEmpty()) {
            System.out.println("Nothing to print.");
            return;
        }

        System.out.println("=== Printing ===");
        System.out.println(text);
        System.out.println("=== End Print ===");
    }

    public boolean failure() {
        return status;
    }

    public void setFailure(boolean status) {
        this.status = status;
    }
}
