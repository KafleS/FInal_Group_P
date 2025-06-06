package Client;

import java.io.Serializable;

public class QuestionInfo implements Serializable {
    private String[] options;
    private int selectionIndex;

    public QuestionInfo(String[] options){
        this.options = options;
    }

    public int getNumOptions() { return options.length; }
    public int getSelection() { return selectionIndex; }
    public String[] getOptions() { return options; }

    public void setSelectionIndex(int index) {
        this.selectionIndex = index;
    }

    public String toString(){
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < options.length; i++){
            string.append(options[i]);
            if (selectionIndex == i) {
                string.append(" <- Selected");
            }
        }
        return string.toString();
    }
}
