package BML;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

public class BlankBallot implements Iterable<Proposition> {
    private final Preamble preamble;
    private final List<Proposition> propositions;

    public BlankBallot(String stringOfJsonData) {
        Preamble preamble = null;
        List<Proposition> propositions = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode node = mapper.readTree(stringOfJsonData);
            preamble = mapper.treeToValue(node.get("preamble"), Preamble.class);
            propositions = mapper.treeToValue(
                    node.get("propositions"),
                    mapper.getTypeFactory().constructCollectionType(
                            List.class,
                            Proposition.class
                    )
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            System.out.println("Ballot Class: Error reading JSON data");
        }

        this.preamble = preamble;
        if (propositions == null) {
            this.propositions = Collections.emptyList();
        }
        else {
            this.propositions = Collections.unmodifiableList(propositions);
        }
    }

    public Preamble getPreamble() { return this.preamble; }

    public Proposition getProposition(int index) {
        return this.propositions.get(index);
    }

    public int getNumPropositions() { return this.propositions.size(); }

    public int getIndexOfProposition(Proposition p) {
        return this.propositions.indexOf(p);
    }

    /**
     * Returns the boolean value in the selectedOptions list.
     *
     * @param propIndex the index of the proposition the option is in
     * @param optionIndex the index of the option
     * */
    public Boolean getOptionValue(int propIndex, int optionIndex) {
        return this.propositions.get(propIndex)
                                .getOptionValue(optionIndex);
    }

    /**
     * This method will modify the boolean value in the selectedOptions
     * array list.
     *
     * @param propIndex the index of the proposition the option is in
     * @param optionIndex the index of the option which was selected
     * @param value the new boolean value
     * @return this method will return true if the value was set,
     * and false otherwise.
     * */
    public boolean markOption(int propIndex, int optionIndex, boolean value) {
        try {
            this.propositions.get(propIndex)
                             .markOption(optionIndex, value);
            return true;
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Ballot Class: Index out of bounds for selectedOptions");
            return false;
        }
    }

    @Override
    public Iterator<Proposition> iterator() {
        return new PropositionIterator();
    }

    private class PropositionIterator implements ListIterator<Proposition> {
        private int currentIndex = -1;

        @Override
        public boolean hasNext() { return currentIndex + 1 < propositions.size(); }

        @Override
        public Proposition next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            currentIndex++;
            return propositions.get(currentIndex);
        }

        @Override
        public int nextIndex() {
            if (!this.hasNext()) {
                return propositions.size();
            }
            return currentIndex + 1;
        }

        @Override
        public boolean hasPrevious() { return currentIndex - 1 > 0; }

        @Override
        public Proposition previous() {
            if (!this.hasPrevious()) {
                throw new NoSuchElementException();
            }
            currentIndex--;
            return propositions.get(currentIndex);
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
        public void set(Proposition booleans) { throw new UnsupportedOperationException(); }
        @Override
        public void add(Proposition booleans) { throw new UnsupportedOperationException(); }
    }
}


