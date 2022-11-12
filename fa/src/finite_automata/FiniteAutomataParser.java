package finite_automata;

import exceptions.FiniteAutomataException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FiniteAutomataParser implements IFiniteAutomataParser {

    private static final String STATES_KEY = "Q";
    private static final String ALPHABET_KEY = "SIGMA";
    private static final String FINAL_STATES_KEY = "F";
    private static final String INITIAL_STATE_KEY = "q";
    private static final String TRANSITIONS_KEY = "TRANSITIONS";
    private static final String TRANSITION_FUNCTION_KEY = "delta";

    private final String filename;

    public FiniteAutomataParser(String filename) {
        this.filename = filename;
    }


    @Override
    public IFiniteAutomata parse() {
        if (filename == null) {
            throw new FiniteAutomataException("You must provide the file that contains the finite automata definition!");
        }

        IFiniteAutomata finiteAutomata = new FiniteAutomata();
        List<String> lines = getFileContentByPath(filename);

        lines.forEach(line -> {
            line = removeAllCharactersFromString(line, " ");
            List<String> lineTokens = List.of(line.split(":"));
            String key = lineTokens.get(0);
            String value = lineTokens.get(1);

            switch (key) {
                case STATES_KEY -> finiteAutomata.setStates(extractStates(value));
                case ALPHABET_KEY -> finiteAutomata.setAlphabet(extractAlphabet(value));
                case FINAL_STATES_KEY -> finiteAutomata.setFinalStates(extractStates(value));
                case INITIAL_STATE_KEY -> finiteAutomata.setInitialState(extractInitialState(value));
                case TRANSITIONS_KEY -> finiteAutomata.setTransitions(extractTransitions(value));
                default -> throw new FiniteAutomataException("Invalid key " + key);
            }
        });

        if (!finiteAutomata.validateSelf()) {
            throw new FiniteAutomataException("Finite automata " + finiteAutomata + " is invalid!");
        }

        return finiteAutomata;
    }

    private List<Transition> extractTransitions(String value) {
        // value = "{delta(q0, 1) = q2, delta(q1, 0) = q3}"

        String sanitized = sanitize(value);
        List<String> stringTransitions = List.of(sanitized.split(";"));

        return stringTransitions.stream()
                .map(stringTransition -> Transition.of(stringTransition, TRANSITION_FUNCTION_KEY))
                .collect(Collectors.toList());
    }

    private String extractInitialState(String value) {
        return sanitize(value);
    }

    private Set<String> extractAlphabet(String value) {
        // value = "{0, 1, 2}"

        String sanitized = sanitize(value);
        Set<String> alphabet = Set.of(sanitized.split(","));

        return alphabet;
    }

    private Set<String> extractStates(String value) {
        // value = "{q0, q1, q2}"

        String sanitized = sanitize(value);
        Set<String> states = Set.of(sanitized.split(","));

        return states;
    }

    private List<String> getFileContentByPath(String filename) {
        try {
            return Files.readAllLines(Path.of(filename));
        } catch (IOException ignored) {
            throw new RuntimeException("Error reading the file " + filename);
        }
    }

    /**
     * Removes whitespaces and curly braces.
     */
    private String sanitize(String string) {
        String whitespacesSanitized = removeAllCharactersFromString(string, " ");
        String openCurlyBracesSanitized = removeAllCharactersFromString(whitespacesSanitized, "\\{");
        String sanitized = removeAllCharactersFromString(openCurlyBracesSanitized, "\\}");

        return sanitized;
    }

    private String removeAllCharactersFromString(String string, String character) {
        return string.replaceAll(character, "");
    }
}
