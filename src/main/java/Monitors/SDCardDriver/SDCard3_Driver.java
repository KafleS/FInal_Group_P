package Monitors.SDCardDriver;

import java.io.IOException;
import java.util.List;

public class SDCard3_Driver {
    private final SDCard3 sdCard3;

    public SDCard3_Driver(SDCard3 sdCard3) {
        this.sdCard3 = sdCard3;
    }

    public List<String> read() throws IOException {
        return sdCard3.getCard().read();
    }

    public void write(String text) throws IOException {
        sdCard3.getCard().write(text);
    }

    public void overwrite(String text) throws IOException {
        sdCard3.getCard().overwrite(text);
    }

    public void eject() {
        sdCard3.getCard().eject();
    }
}