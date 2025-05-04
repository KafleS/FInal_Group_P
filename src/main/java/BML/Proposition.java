package BML;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

public class Proposition implements Iterable<String> {
    private final int id;
    private final String title;
    private final String description;
    private final List<String> options;
    private final int maxSelections;
    private List<Boolean> isSelectedList;

    @JsonCreator
    public Proposition(
            @JsonProperty("id") int id,
            @JsonProperty("title") String title,
            @JsonProperty("description") String description,
            @JsonProperty("options") List<String> options,
            @JsonProperty("maxSelections") int maxSelections) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.options = Collections.unmodifiableList(options);
        this.maxSelections = maxSelections;
        this.isSelectedList = new ArrayList<>();
        for (int i = 0; i < options.size(); i++) {
            this.isSelectedList.add(false);
        }
    }

    public int getNumOptions() { return this.options.size(); }

    public int getId() { return this.id; }

    public String getTitle() { return this.title; }

    public String getDescription() { return this.description; }

    public String getOption(int index) { return this.options.get(index); }

    public int getMaxSelections() { return this.maxSelections; }

    public int getIndexOfOption(String option) {
        return this.options.indexOf(option);
    }

    public Boolean getOptionValue(int index) {
        return this.isSelectedList.get(index);
    }

    public void markOption(int index, boolean value) {
        this.isSelectedList.set(index, value);
    }

    public boolean isSelected(String option) {
        int index = this.options.indexOf(option);
        return this.isSelectedList.get(index);
    }

    @Override
    public Iterator<String> iterator() {
        return new optionsIterator();
    }

    @Override
    public boolean equals(Object o) {
        // return true if the propositions have the same ID
        return o instanceof Proposition &&
                this.id == ((Proposition) o).getId();
    }

    @Override
    public String toString() {
        return "Proposition { title = " + title + ", " + " id=" + id + " }";
    }

    private class optionsIterator implements ListIterator<String> {
        private int currentIndex = -1;

        @Override
        public boolean hasNext() {
            return currentIndex + 1 < options.size();
        }
        @Override
        public String next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            currentIndex++;
            return options.get(currentIndex);
        }

        @Override
        public int nextIndex() {
            if (!this.hasNext()) {
                return options.size();
            }
            return currentIndex + 1;
        }

        @Override
        public boolean hasPrevious() {
            return currentIndex - 1 >= 0;
        }

        @Override
        public String previous() {
            if (!this.hasPrevious()) {
                throw new NoSuchElementException();
            }
            currentIndex--;
            return options.get(currentIndex);
        }

        @Override
        public int previousIndex() {
            if (!this.hasPrevious()) {
                return -1;
            }
            return currentIndex - 1;
        }

        @Override
        public void remove() { throw new UnsupportedOperationException(); }
        @Override
        public void set(String pair) { throw new UnsupportedOperationException(); }
        @Override
        public void add(String pair) { throw new UnsupportedOperationException(); }
    }
}
