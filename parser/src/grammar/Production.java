package grammar;

import exceptions.GrammarException;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Production {
    public String source;
    public List<String> targets;

    public Production(String productionString) {
        List<String> tokens = List.of(productionString.split("->"));

        if (tokens.size() != 2) {
            throw new GrammarException("Production format is invalid: " + productionString);
        }

        this.source = tokens.get(0).trim();
        this.targets = Stream.of(tokens.get(1).split("\\|")).map(String::trim).collect(Collectors.toList());

        validateSelf();
    }

    private void validateSelf() {
        if (source.length() > 1) {
            throw new GrammarException("Having terminals or non-terminals with length > 1 will cause system malfunctions: " + source);
        }
    }

    @Override
    public String toString() {
        return "Production{" +
            "source='" + source + '\'' +
            ", targets=" + targets +
            '}';
    }
}
