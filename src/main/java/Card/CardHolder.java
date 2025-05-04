// --- CardHolder.java ---
package Card;

public class CardHolder {
    private CardReaderDriver driver;

    public CardHolder(CardReaderDriver driver) {
        this.driver = driver;
    }

    public void insertCard(String data) {
        driver.insert(data);
    }

    public String readCard() {
        return driver.read();
    }

    public void eraseCard() {
        driver.erase();
    }

    public void ejectCard() {
        driver.eject();
    }

    public boolean hasFailure() {
        return driver.hasFailure();
    }
}
