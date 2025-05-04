package Monitors.SDCardDriver;

import simple.SDCard;

public class SDCard3 {
    private final SDCard card;

    public SDCard3(SDCard.Operation op) {

        //copy of voter1 card
        card = new SDCard(2, op);
    }

    public SDCard getCard() {
        return card;
    }
}