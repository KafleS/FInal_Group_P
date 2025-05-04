// --- VotingControl.java ---
package Control;

import Card.*;

public class VotingControl {
    private CardHolder cardHolder;

    public VotingControl(CardHolder cardHolder) {
        this.cardHolder = cardHolder;
    }

    public void notifyCardInserted(String cardData) {
        System.out.println("VotingControl received card data: " + cardData);

        if (cardHolder.hasFailure()) {
            System.out.println("Error: Card reader failure. Cannot process card.");
            return;
        }

        cardHolder.insertCard(cardData);
        String readData = cardHolder.readCard();
        System.out.println("Read from card: " + readData);

        // Add more logic here: validation, authorization, etc.

        cardHolder.eraseCard();
        cardHolder.ejectCard();
    }
}
