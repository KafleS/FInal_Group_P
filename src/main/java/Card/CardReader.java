package Card;

public class CardReader {
    public boolean isCardInserted = false;
    private String cardData = null;
    private boolean status = false;

    public void readInput(String socketInput) {
        if (socketInput == null) {
            System.out.println("CardReader ignored: null input.");
            return;
        }

        if (socketInput.startsWith("CRreader:")) {
            String data = socketInput.substring("CRreader:".length());
            insertCard(data);
            System.out.println("[CardReader] Card inserted from socket: " + data);
        } else {
            System.out.println("[CardReader] Ignored non-card input: " + socketInput);
        }
    }

    public void insertCard(String data) {
        isCardInserted = true;

        if (data.startsWith("CRreader:")) {
            data = data.substring("CRreader:".length());
        }

        cardData = data;
        CardType type = CardType.resolve(cardData);
        System.out.println("[CardReader] Card inserted with ID: " + cardData + " | Type: " + type);
    }

    public String readCard() {
        return isCardInserted ? cardData : null;
    }

    public void ejectCard() {
        System.out.println("[CardReader] Card ejected.");
        cardData = null;
        isCardInserted = false;
    }

    public void eraseCard() {
        if (isCardInserted) {
            cardData = "";
            System.out.println("[CardReader] Card data erased.");
        }
    }

    public CardType getCardType() {
        if (!isCardInserted || cardData == null) {
            return CardType.UNKNOWN;
        }
        return CardType.resolve(cardData);
    }

    public String getCardInfo() {
        if (!isCardInserted || cardData == null) return "No card inserted.";
        return "Card ID: " + cardData + " | Type: " + CardType.resolve(cardData);
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
