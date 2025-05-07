// --- CardHolder.java ---
package Card;

import java.io.IOException;

public class CardHolder {
    private CardReaderDriver driver;
    private CardReader reader;

    public CardHolder(CardReaderDriver driver) {
        this.driver = driver;
        this.reader = driver.getReader(); // Reuse the actual CardReader instance
        // Needed to access card type functionality
    }

    public void insertCard(String data) {
        reader.readInput(data);                  // Reads & extracts cardId
        String cleanId = data.startsWith("CRreader:") ? data.substring("CRreader:".length()) : data;
        driver.insert(cleanId);                  // Inserts only the raw card ID
    }


    public String readCard() {
        return driver.read();
    }

    public void eraseCard() {
        driver.erase();
    }

    public void ejectCard() {
        reader.ejectCard();            // Clear type tracking
        driver.eject();
    }

    public boolean hasFailure() {
        return driver.hasFailure();
    }

    public CardType getCardType() {
        try {
            return reader.getCardType();
        } catch (IOException e) {
            e.printStackTrace();
            return CardType.UNKNOWN;
        }
    }
}
