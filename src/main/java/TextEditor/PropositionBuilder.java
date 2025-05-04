package TextEditor;

import java.util.ArrayList;
import java.util.List;

public class PropositionBuilder {
    private int id;
    private String title;
    private String description;
    private List<String> options;
    private int maxSelections;

    public PropositionBuilder() {}

    public PropositionBuilder(
            int id,
            String title,
            String description,
            List<String> options,
            int maxSelections
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.options = options;
        this.maxSelections = maxSelections;
    }

    public int getId() {
        return id;
    }

    public String getTitle() { return this.title; }

    public String getDescription() { return this.description; }

    public List<String> getOptions() { return this.options; }

    public int getMaxSelections() { return this.maxSelections; }

    public void setTitle(String title) { this.title = title; }

    public void setDescription(String description) { this.description = description; }

    public void setOptions(List<String> options) { this.options = options; }

    public void setMaxSelections(int maxSelections) { this.maxSelections = maxSelections; }

    @Override
    public String toString() {
        return "Proposition { title = " + title + ", " + " id=" + id + '}';
    }
}
