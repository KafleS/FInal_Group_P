package Hardwares.SDCards;

import java.io.IOException;
import java.util.List;

public class SDCard2_Driver {
    private final SDCard2 sdCard2;

    public SDCard2_Driver(SDCard2 sdCard2) {
        this.sdCard2 = sdCard2;
    }

    public List<String> read() throws IOException {
        return sdCard2.getCard().read();
    }

    public void write(String text) throws IOException {
        sdCard2.getCard().write(text);
    }

    public void overwrite(String text) throws IOException {
        sdCard2.getCard().overwrite(text);
    }

    public void eject() {
        sdCard2.getCard().eject();
    }
}