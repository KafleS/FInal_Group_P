package Hardwares.SDCards;

public class SDCard1 {
    private final SDCard card;
    private boolean status = false;

    public SDCard1(SDCard.Operation op) {

        //ballot card
        card = new SDCard(0, op);
    }

    public void setFailure(boolean status) {
        this.status = status;
        if (status) System.out.println("[SD1] FAILURE detected!");
    }

    public SDCard getCard() {
        return card;
    }
}