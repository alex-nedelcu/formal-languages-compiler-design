package parser;

import grammar.IGrammar;
import grammar.Production;
import grammar.SymbolSequence;

import java.util.*;


public class Parser implements IParser {
    private static final String EPSILON = "EPSILON";
    private Map<String, Set<String>> first;
    private Map<String, Set<String>> follow;
    private final IGrammar grammar;

    public Parser(IGrammar grammar) {
        this.grammar = grammar;
        assert (this.grammar.isContextFree());

        this.first = new HashMap<>();
        initializeFirst();
        computeFirst();

        this.follow = new HashMap<>();
        initializeFollow();
        computeFollow();
    }

    private void initializeFirst() {
        Set<String> nonTerminals = grammar.getNonTerminals();
        Set<String> terminals = grammar.getTerminals();
        Set<Production> productions = new HashSet<>(grammar.getProductions());

        // initialize each non-terminal first with an empty list
        for (String nonTerminal : nonTerminals) {
            first.put(nonTerminal, new HashSet<>());
        }

        for (Production production : productions) {
            String source = production.getUniqueSource();
            HashSet<SymbolSequence> targets = new HashSet<>(production.targets); // e.g. [ SymbolSequence(symbols={'a', 'B'}), SymbolSequence(symbols={'EPSILON'}) ] when source -> aB | EPSILON

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

    private void computeFirst() {
        var current = new HashMap<String, Set<String>>();
        Set<String> nonTerminals = grammar.getNonTerminals();

        for (String symbol : first.keySet()) {
            Set<String> value = new HashSet<>(first.get(symbol));
            Set<Production> productionsForSymbolTemp = new HashSet<>(grammar.getProductionsByNonTerminal(symbol));
            HashSet<SymbolSequence> productionsForSymbol = new HashSet<>();
            for (Production production : productionsForSymbolTemp) {
                productionsForSymbol.addAll(production.targets);
            }

            for (var production : productionsForSymbol) {
                var nonTerminalSymbolsFirst = new ArrayList<String>();
                boolean nonEmpty = true;

                if (nonTerminals.contains(production.getSymbols().get(0))) {
                    for (var element : production.getSymbols()) {
                        if (nonTerminals.contains(element)) {
                            if (!first.containsKey(element)) {
                                nonEmpty = false;
                                continue;
                            }
                            nonTerminalSymbolsFirst.add(element);
                        }
                    }

                    if (nonEmpty && nonTerminalSymbolsFirst.size() >= 2) {
                        Set<String> result = new HashSet<>();
                        result.addAll(first.get(nonTerminalSymbolsFirst.get(0)));
                        result.addAll(first.get(nonTerminalSymbolsFirst.get(1)));

                        for (int index = 2; index < nonTerminalSymbolsFirst.size(); index++) {
                            result.addAll(first.get(nonTerminalSymbolsFirst.get(index)));
                        }

                        value.addAll(result);
                    } else if (nonEmpty && nonTerminalSymbolsFirst.size() == 1) {
                        value = new HashSet<>(first.get(nonTerminalSymbolsFirst.get(0)));
                    }
                }
            }

            current.put(symbol, value);
        }

        if (!first.equals(current)) {
            first = current;
            computeFirst();
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
                follow.put(nonTerminal, new HashSet<>(singletonEpsilonList));
            } else {
                follow.put(nonTerminal, new HashSet<>());
            }
        }
    }

    private void computeFollow() {
        Map<String, Set<String>> current = new HashMap<String, Set<String>>();
        Set<String> terminals = grammar.getTerminals();
        Set<String> nonTerminals = grammar.getNonTerminals();
        List<Production> productions = grammar.getProductions();

        for (String symbol : nonTerminals) {
            Set<String> currentValue = follow.get(symbol);
            Set<Production> matchingProductions = getProductionsWithSymbol(new HashSet<>(productions), symbol);

            for (var production : matchingProductions) {
                String leftSide = production.getUniqueSource();
                SymbolSequence rightSide = production.targets.get(0);

                int indexOfSymbol = rightSide.getSymbols().indexOf(symbol);
                if (indexOfSymbol < rightSide.getSymbols().size() - 1) {
                    String valueAfterSymbol = rightSide.getSymbols().get(indexOfSymbol + 1);
                    if (terminals.contains(valueAfterSymbol)) {
                        currentValue.add(valueAfterSymbol);
                    } else {
                        Set<String> neighbourFirstValue = first.getOrDefault(valueAfterSymbol, null);
                        boolean foundEpsilon = false;

                        if (neighbourFirstValue != null) {
                            for (var value : neighbourFirstValue) {
                                if (value.equals(EPSILON)) {
                                    foundEpsilon = true;
                                } else {
                                    currentValue.add(value);
                                }
                            }
                        }

                        if (foundEpsilon) {
                            currentValue.addAll(follow.get(leftSide));
                        }
                    }
                } else if (indexOfSymbol == rightSide.getSymbols().size() - 1) {
                    currentValue.addAll(follow.get(leftSide));
                }
            }

            current.put(symbol, currentValue);
        }

        if (!areMapsEqual(follow, current)) {
            follow = current;
            computeFollow();
        }
    }

    private Set<Production> getProductionsWithSymbol(Set<Production> productions, String symbol) {
        var result = new HashSet<Production>();
        for (var production : productions) {
            for (var rightSide : production.targets) {
                if (rightSide.getSymbols().contains(symbol)) {
                    result.add(new Production(production.getUniqueSource(), rightSide));
                }
            }
        }
        return result;
    }

    private boolean symbolBelongsToFirstOfKey(String symbol, String key) {
        return first != null && first.containsKey(key) && first.get(key).contains(symbol);
    }

    private boolean isEpsilon(String symbol) {
        return symbol != null && symbol.trim().equalsIgnoreCase(EPSILON);
    }

    private boolean areMapsEqual(Map<String, Set<String>> first, Map<String, Set<String>> second) {
        Set<String> firstMapKeys = first.keySet();
        Set<String> secondMapKeys = second.keySet();

        if (!areEqual(firstMapKeys, secondMapKeys)) {
            return false;
        }

        for (String key : firstMapKeys) {
            Set<String> valueFromFirst = first.get(key);
            Set<String> valueFromSecond = second.get(key);

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
    public Map<String, Set<String>> getFirst() {
        return first;
    }

    @Override
    public Map<String, Set<String>> getFollow() {
        return follow;
    }
}
