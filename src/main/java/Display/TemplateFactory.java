package Display;

import BML.BlankBallot;
import BML.Proposition;

import java.util.ArrayList;
import java.util.List;

public class TemplateFactory {
    public static List<Template> fromBallot(BlankBallot blankBallot) {
        List<Template> templates = new ArrayList<>();

        for (int i = 0; i < blankBallot.getNumPropositions(); i++) {
            Proposition p = blankBallot.getProposition(i);

            String instruction = p.getMaxSelections() == 1
                    ? "Please select only one option below:"
                    : "Please select up to " + p.getMaxSelections() + " options below:";

            // Use getOption(index) instead of getOptions()
            String[] options = new String[p.getNumOptions()];
            for (int j = 0; j < p.getNumOptions(); j++) {
                options[j] = p.getOption(j);
            }

            templates.add(new Template(
                    p.getId(),
                    p.getTitle(),
                    p.getDescription(),
                    instruction,
                    new ButtonData(i != 0),
                    new ButtonData(i == blankBallot.getNumPropositions() - 1),
                    new ButtonData(i != blankBallot.getNumPropositions() - 1),
                    new QuestionInfo(options)
            ));
        }

        return templates;
    }
}
