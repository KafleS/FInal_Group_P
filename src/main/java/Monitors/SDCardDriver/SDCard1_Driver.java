package Monitors.SDCardDriver;

import java.io.IOException;
import java.util.List;

public class SDCard1_Driver {
    private final SDCard1 sdCard1;

    public SDCard1_Driver(SDCard1 sdCard1) {
        this.sdCard1 = sdCard1;
    }

    public List<String> read() throws IOException {
        return sdCard1.getCard().read();
    }

    public void write(String text) throws IOException {
        sdCard1.getCard().write(text);
    }

    public void overwrite(String text) throws IOException {
        sdCard1.getCard().overwrite(text);
    }

    public void eject() {
        sdCard1.getCard().eject();
    }
}