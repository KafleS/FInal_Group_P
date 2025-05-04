package Monitors.SDCardDriver;

import simple.SDCard;

public class SDCard1 {
    private final SDCard card;

    public SDCard1(SDCard.Operation op) {

        //ballot card
        card = new SDCard(0, op);
    }

    public SDCard getCard() {
        return card;
    }
}