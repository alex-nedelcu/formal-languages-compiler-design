package parser;

import grammar.IGrammar;
import grammar.Production;
import grammar.SymbolSequence;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Parser implements IParser {

    private static final String EPSILON = "EPSILON";
    private final Map<String, Set<String>> first;
    private final Map<String, Set<String>> follow;
    private final IGrammar grammar;

    public Parser(IGrammar grammar) {
        this.grammar = grammar;
        assert (this.grammar.isContextFree());

        this.first = new HashMap<>();
        initializeFirst();

        this.follow = new HashMap<>();
        initializeFollow();
    }

    private void initializeFirst() {
        Set<String> nonTerminals = grammar.getNonTerminals();
        Set<String> terminals = grammar.getTerminals();
        List<Production> productions = grammar.getProductions();

        // initialize each non-terminal first set with an empty set
        for (String nonTerminal : nonTerminals) {
            first.put(nonTerminal, new HashSet<>());
        }

        for (Production production : productions) {
            String source = production.getUniqueSource();
            List<SymbolSequence> targets = production.targets; // e.g. [ SymbolSequence(symbols={'a', 'B'}), SymbolSequence(symbols={'EPSILON'}) ] when source -> aB | EPSILON

            for (SymbolSequence target : targets) {
                List<String> targetTokens = target.getSymbols(); // e.g. ['a', 'B']
                String firstSymbolFromTarget = targetTokens.get(0);

                if (isEpsilon(firstSymbolFromTarget)) {
                    first.get(source).add(firstSymbolFromTarget);
                }

                if (terminals.contains(firstSymbolFromTarget) && !symbolBelongsToFirstOfKey(firstSymbolFromTarget, source)) {
                    first.get(source).add(firstSymbolFromTarget);
                }
            }
        }
    }

    private void initializeFollow() {

    }

    private boolean symbolBelongsToFirstOfKey(String symbol, String key) {
        return first != null && first.containsKey(key) && first.get(key).contains(symbol);
    }

    private boolean isEpsilon(String symbol) {
        return symbol != null && symbol.trim().equalsIgnoreCase(EPSILON);
    }

    @Override
    public Map<String, Set<String>> getFirst() {
        return first;
    }

    @Override
    public Map<String, Set<String>> getFollow() {
        return follow;
    }
}
