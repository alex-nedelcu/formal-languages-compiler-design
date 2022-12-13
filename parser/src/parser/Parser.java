package parser;

import grammar.IGrammar;
import grammar.Production;
import grammar.SymbolSequence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Parser implements IParser {

    private static final String EPSILON = "EPSILON";
    private final Map<String, List<String>> first;
    private final Map<String, List<String>> follow;
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

        // initialize each non-terminal first with an empty list
        for (String nonTerminal : nonTerminals) {
            first.put(nonTerminal, new ArrayList<>());
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
        Set<String> nonTerminals = grammar.getNonTerminals();
        String startingSymbol = grammar.getStartingSymbol();

        // initialize follow of starting symbol with a singleton list containing EPSILON
        // for all the other non-terminals, initialize their follow with an empty list
        for (String nonTerminal : nonTerminals) {
            if (nonTerminal.equals(startingSymbol)) {
                List<String> singletonEpsilonList = new ArrayList<>(Collections.singleton(EPSILON));
                follow.put(nonTerminal, singletonEpsilonList);
            } else {
                follow.put(nonTerminal, new ArrayList<>());
            }
        }
    }


    private boolean symbolBelongsToFirstOfKey(String symbol, String key) {
        return first != null && first.containsKey(key) && first.get(key).contains(symbol);
    }

    private boolean isEpsilon(String symbol) {
        return symbol != null && symbol.trim().equalsIgnoreCase(EPSILON);
    }

    private boolean areMapsEqual(Map<String, List<String>> first, Map<String, List<String>> second) {
        Set<String> firstMapKeys = first.keySet();
        Set<String> secondMapKeys = second.keySet();

        if (!areEqual(firstMapKeys, secondMapKeys)) {
            return false;
        }

        for (String key : firstMapKeys) {
            List<String> valueFromFirst = first.get(key);
            List<String> valueFromSecond = second.get(key);

            if (!areEqual(valueFromFirst, valueFromSecond)) {
                return false;
            }
        }

        return true;
    }

    private boolean areEqual(Collection<String> first, Collection<String> second) {
        return first.equals(second);
    }

    @Override
    public Map<String, List<String>> getFirst() {
        return first;
    }

    @Override
    public Map<String, List<String>> getFollow() {
        return follow;
    }
}
