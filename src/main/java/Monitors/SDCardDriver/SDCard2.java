package Monitors.SDCardDriver;

import  simple.SDCard;

public class SDCard2 {

    private final SDCard card;

    public SDCard2(SDCard.Operation op) {

        //voter1 card
        card = new SDCard(1,op);
    }

    public  SDCard getCard() {
        return card;
    }
}
