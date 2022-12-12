package grammar;

import exceptions.GrammarException;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Production {

    public SymbolSequence sources;
    public List<SymbolSequence> targets;

    public Production(String productionString) {
        List<String> productionTokens = ensureValidity(splitByArrow(productionString));

        this.sources = extractSource(productionTokens.get(0));
        this.targets = extractTargets(productionTokens.get(1));
    }

    private List<String> splitByArrow(String productionString) {
        // receives 'A -> b | c.D.E | EPSILON' and returns ['A', 'b | c.D.E | EPSILON']
        List<String> tokens = List.of(productionString.split("->"));

        return tokens;
    }

    private List<String> ensureValidity(final List<String> tokens) {
        if (tokens == null) {
            throw new GrammarException("Received null tokens list.");
        }

        if (tokens.size() != 2) {
            throw new GrammarException("Production format is invalid. Obtained after tokenization: " + tokens);
        }

        return tokens;
    }

    private SymbolSequence extractSource(String lhs) {
        // receives 'S' or 'a.S' and returns SymbolSequence(symbols=['S']) or SymbolSequence(symbols=['a', 'S'])
        return new SymbolSequence(lhs);
    }

    private List<SymbolSequence> extractTargets(String rhs) {
        // receives 'b | c.D.E | EPSILON' and returns
        // [ SymbolSequence(symbols=['b']) , SymbolSequence(symbols=['c', 'D', 'E']) , SymbolSequence(symbols=['EPSILON']) ]
        List<String> dottedRhsSequences = Stream.of(rhs.split("\\|")).map(String::trim).toList();
        return dottedRhsSequences.stream().map(SymbolSequence::new).collect(Collectors.toList());
    }

    public String getUniqueSource() {
        assert (sources.getSymbols().size() == 1);
        return sources.getSymbols().get(0);
    }

    public boolean hasUniqueSource() {
        return sources.getSymbols().size() == 1;
    }

    @Override
    public String toString() {
        return "Production{" +
            "sources=" + sources +
            ", targets=" + targets +
            '}';
    }
}
