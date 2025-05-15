package Control;

import Card.*;
import Client.ButtonData;
import Client.QuestionInfo;
import Client.Template;
import Hardwares.Monitor;
import Managers.AdminManager;
import Managers.VotingManager;
import Hardwares.Battery.BatteryDriver;
import Hardwares.Printer.PrinterDriver;
import Hardwares.SDCards.SDCard2_Driver;
import Hardwares.SDCards.SDCard3_Driver;
import Hardwares.SDCards.SDCard1_Driver;
import Hardwares.Screens.ScreenDriver;
import Hardwares.latch.LatchDriver;


import java.io.ObjectOutputStream;
import java.io.PrintWriter;

public class VotingControl {
    private final CardHolder cardHolder;
    private final Monitor monitor;
    private final VotingManager votingManager;

    private final AdminManager adminManager;

    public VotingControl(
            CardHolder cardHolder,
            LatchDriver latchDriver,
            BatteryDriver batteryDriver,
            PrinterDriver printerDriver,
            SDCard1_Driver sdCard1,
            SDCard2_Driver sdCard2,
            SDCard3_Driver sdCard3,
            VotingManager votingManager,
            ScreenDriver screenDriver, AdminManager adminManager
    ) {
        this.cardHolder = cardHolder;
        this.votingManager = votingManager;
        this.adminManager = adminManager;

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

    public void notifyCardInserted(String cardData, ObjectOutputStream out) {
        try {
            System.out.println("VotingControl received card data: " + cardData);

            if (cardHolder.hasFailure()) {
                out.writeObject("[Screen] Card reader failure. Cannot process card.");
                out.flush();
                return;
            }

            cardHolder.insertCard(cardData);

            CardType type = cardHolder.getCardType();

            System.out.println("[VotingControl] CardType resolved to: " + type);
            switch (type) {
                case VOTER -> {
                    String readData = cardHolder.readCard();
                    System.out.println(" in main voting control: " + readData);

                    if (!adminManager.isVotingOpen()) {
                        out.writeObject("Voting is not open. Please contact an official.");
                        out.flush();
                        System.out.println("[VotingControl]  Voting not open. Rejecting voter.");
                        return;
                    }
                    out.writeObject("[Screen] Voter card accepted. Welcome: " + readData);
                    out.flush();
                    votingManager.setVotingOpen(adminManager.isVotingOpen());
                    votingManager.start();
                }
                case OFFICIAL -> {
                    System.out.println("[VotingControl] Admin card detected.");

                    out.writeObject("[Screen] Admin card accepted. Launching Admin Panel...");
                    out.flush();

                    ButtonData prev = new ButtonData(false);
                    ButtonData submit = new ButtonData(true);
                    ButtonData next = new ButtonData(false);
                    QuestionInfo adminQ = new QuestionInfo(new String[] {
                            "Open Voting Session", "Close Voting Session"
                    });


                    // Create Template
                    Template adminTemplate = new Template(
                            999, // some ID
                            "Admin Panel",
                            "This is a secure control panel for election officials.",
                            "Select a management action below:",
                            prev,
                            submit,
                            next,
                            adminQ
                    );

                    // Send to screen
                    ScreenDriver.getInstance().present(adminTemplate);
                    System.out.println("[VotingControl] ✅ Admin template sent to screen.");


                    // ✅ NEW: Poll for user input and process it
                    Template result = ScreenDriver.getInstance().waitForTemplateResponse();
                    if (result != null) {
                        processAdminTemplate(result);

                        // ✅ Admin session cleanup
                        cardHolder.ejectCard();
                        cardHolder.eraseCard();
                        System.out.println("[VotingControl] 🔁 Admin card ejected and cleared.");

                        out.writeObject("resetToLogin");
                        out.flush();



                    }
                }


                case UNKNOWN -> {
                    out.writeObject("[Screen] Unknown or invalid card. Please try again.");
                    out.flush();
                }
            }
        } catch (Exception e) {
            System.err.println("[VotingControl] Error sending response: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void processAdminTemplate(Template template) {
        if (template == null || !template.getTitle().equalsIgnoreCase("Admin Panel")) {
            System.err.println("[VotingControl] ⚠ Invalid admin template received.");
            return;
        }

        String[] options = template.getQuestionData().getOptions();
        int selectedIndex = template.getQuestionData().getSelection();

        if (selectedIndex < 0 || selectedIndex >= options.length) {
            System.out.println("[VotingControl] ⚠ No valid admin action selected.");
            return;
        }

        String selectedAction = options[selectedIndex];
        System.out.println("[VotingControl] Admin selected: " + selectedAction);

        if (selectedAction.equalsIgnoreCase("Open Voting Session")) {
            adminManager.openVotingSession();
        } else if (selectedAction.equalsIgnoreCase("Close Voting Session")) {
            adminManager.closeVotingSession();
        } else {
            System.out.println("[VotingControl] ⚠ Unknown admin action: " + selectedAction);
        }
    }


    public  void registerClient(PrintWriter out) {
        monitor.registerClient(out);
    }

    public void stopMonitor() {
        if (monitor != null) {
            monitor.stopMonitoring();
        }
    }
}
