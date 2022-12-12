package parser;

import grammar.IGrammar;
import grammar.Production;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Parser implements IParser {

    @Override
    public List<String> first(IGrammar grammar, String inputSymbol) {
        List<Map<String, List<String>>> history = new ArrayList<>();
        Map<String, List<String>> currentFirst = new HashMap<>();

        Set<String> terminals = grammar.getTerminals();

        // for each terminal, initialize first(terminal) with a list containing the terminal itself
        for (String terminal : terminals) {
            currentFirst.put(terminal, List.of(terminal));
        }

        List<Production> productions = grammar.getProductions();

        // initialization for non-terminals
        for (Production production : productions) {
            String source = production.source;
            List<String> targets = production.targets;

            for (String target : targets) {
                String firstTargetSymbol = String.valueOf(target.charAt(0));
                if (terminals.contains(firstTargetSymbol)) {
                    currentFirst.putIfAbsent(source, List.of());
                    currentFirst.get(source).add(firstTargetSymbol);
                }
            }
        }

        history.add(currentFirst);
        Set<String> nonTerminals = grammar.getNonTerminals();

        do {
            Map<String, List<String>> previousFirst = history.get(history.size() - 1);

            for (String nonTerminal : nonTerminals) {
                List<Production> productionsByNonTerminal = grammar.getProductionsByNonTerminal(nonTerminal);
                for (Production production : productionsByNonTerminal) {
                    List<String> targets = production.targets; // aA|B|C

                    for (String target: targets) { // aA
                        boolean foundEmptyInPreviousFirst = false;

                        for (Character symbol : target.toCharArray()) {
                            if (!previousFirst.containsKey(String.valueOf(symbol))) {
                                foundEmptyInPreviousFirst = true;
                                break;
                            }
                        }

                        if (!foundEmptyInPreviousFirst) {
                            currentFirst.get(nonTerminal).add(getFirstSymbolFromConcatenation(target, previousFirst));
                        }
                    }
                }
            }
        } while (!Objects.equals(history.get(history.size() - 1), history.get(history.size() - 2)));

        return currentFirst.get(inputSymbol);
    }

    private String getFirstSymbolFromConcatenation(String target, Map<String, List<String>> previousFirst) {
        // target = BCD
        // previousFirst[B] (+) previousFirst[C] (+) previousFirst[D] -> take first symbol from this
        return null;
    }
}
