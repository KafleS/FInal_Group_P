package Card;


import java.io.IOException;

public class CardReader {
    public boolean isCardInserted = false;
    private String cardData = null;

    private boolean status = false;

    public void insertCard(String data) {
        isCardInserted = true;
        cardData = data;
        System.out.println("Card inserted with data: " + data);
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

    // Demo functions below
    public void setFailure(boolean status) {
        this.status = status;
    }
}
