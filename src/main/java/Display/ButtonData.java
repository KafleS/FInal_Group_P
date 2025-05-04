package Display;

import java.io.Serializable;

public class ButtonData implements Serializable {
    private boolean isActive;
    private boolean pressed;

    // Constructor
    public ButtonData(boolean isActive) {
        this.isActive = isActive;
        this.pressed = false;
    }

    public void pressButton(){
        pressed = true;
    }
    public void resetButton() {pressed = false;}
    public boolean getActive() {return isActive;}
    public boolean getPressed() {return pressed;}
}