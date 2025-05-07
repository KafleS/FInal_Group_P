package Card;

import java.io.IOException;

public class CardReader {
    public boolean isCardInserted = false;
    private String cardData = null;

    private boolean status = false;

    // NEW METHOD: called from backend to handle socket input like "CRreader:V12345678"
    public void readInput(String socketInput) {
        if (socketInput == null) {
            System.out.println("CardReader ignored: null input.");
            return;
        }

        // Accept only card-read instructions
        if (socketInput.startsWith("CRreader:")) {
            String data = socketInput.substring("CRreader:".length());
            insertCard(data);
            System.out.println("[CardReader] Card inserted: " + data);
        } else {
            System.out.println("[CardReader] Ignored non-card input: " + socketInput);
        }
    }


    public void insertCard(String data) {
        isCardInserted = true;

        // Strip CRreader: prefix if accidentally included
        if (data.startsWith("CRreader:")) {
            data = data.substring("CRreader:".length());
        }

        cardData = data;
        CardType type = CardType.resolve(cardData);
        System.out.println("Card inserted with data: " + cardData + " | Type resolved: " + type);
    }


    public String readCard() {
        return isCardInserted ? cardData : null;
    }

    public void ejectCard() {
        System.out.println("Card ejected.");
        cardData = null;
        isCardInserted = false;
    }

    public void eraseCard() {
        if (isCardInserted) {
            cardData = "";
            System.out.println("Card data erased.");
        }
    }

    public CardType getCardType() throws IOException {
        if (!isCardInserted || cardData == null) {
            throw new IOException("No card present to check type.");
        }
        return CardType.resolve(cardData);
    }

    public boolean isCardPresent() {
        return isCardInserted;
    }

    public boolean failure() {
        return status;
    }

    public void setFailure(boolean status) {
        this.status = status;
    }
}
