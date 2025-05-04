package Card;

public class CardReaderDriver {
    private CardReader reader;

    public CardReaderDriver(CardReader reader) {
        this.reader = reader;
    }

    public void insert(String data) {
        reader.insertCard(data);
    }

    public String read() {
        return reader.readCard();
    }

    public void eject() {
        reader.ejectCard();
    }

    public void erase() {
        reader.eraseCard();
    }

    public boolean hasFailure() {
        return reader.failure();
    }
}