package parser;

import grammar.IGrammar;
import grammar.Production;
import grammar.SymbolSequence;

import java.util.*;
import java.util.stream.Collectors;


public class Parser implements IParser {
    private static final String EPSILON = "EPSILON";
    private Map<String, List<String>> first;
    private Map<String, List<String>> follow;
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
            first.put(nonTerminal, new ArrayList<>());
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
        Map<String, List<String>> currentFirst;
        Map<String, List<String>> newFirst = new HashMap<>(first);
        Set<String> nonTerminals = grammar.getNonTerminals();

        do {
            currentFirst = new HashMap<>(newFirst);

            for (String source : currentFirst.keySet()) {
                List<String> currentFirstBySource = new ArrayList<>(currentFirst.get(source));
                boolean canCompute = true;
                List<String> allNextNonTerminals = new ArrayList<>();
                List<SymbolSequence> allTargets = grammar.getProductionsByNonTerminal(source)
                    .stream()
                    .map(production -> production.targets)
                    .flatMap(List::stream)
                    .toList();

                for (SymbolSequence target : allTargets) {
                    String firstSymbol = target.getSymbols().get(0);

                    if (nonTerminals.contains(firstSymbol)) {
                        allNextNonTerminals.add(firstSymbol);
                    }
                }


                for (String nextNonTerminal : allNextNonTerminals) {
                    List<String> currentFirstForNextNonTerminal = currentFirst.get(nextNonTerminal);

                    if (currentFirstForNextNonTerminal == null) {
                        canCompute = false;
                    }
                }

                if (canCompute) {
                    List<String> currentFirstForSource = new ArrayList<>(first.get(source));
                    List<List<String>> allCurrentFirstsForNextNonTerminals = allNextNonTerminals
                        .stream()
                        .map(currentFirst::get)
                        .toList();
                    List<String> result;

                    // concatenation of length 1 between all elements of allCurrentFirstsForNextNonTerminals
                    int howMany = allCurrentFirstsForNextNonTerminals.size();

                    if (howMany == 0) {
                        continue;
                    } else if (howMany == 1) {
                        result = allCurrentFirstsForNextNonTerminals.get(0);
                    } else if (howMany == 2) {
                        result = concatenate(
                            allCurrentFirstsForNextNonTerminals.get(0),
                            allCurrentFirstsForNextNonTerminals.get(1)
                        );
                    } else {
                        int index = 0;
                        result = allCurrentFirstsForNextNonTerminals.get(index);

                        do {
                            index += 1;
                            List<String> next = allCurrentFirstsForNextNonTerminals.get(index);
                            result = concatenate(result, next);
                        } while (index < howMany - 1);
                    }

                    List<String> newFirstBySource = new ArrayList<>(currentFirstBySource);
                    newFirstBySource.addAll(result);
                    newFirst.put(source, newFirstBySource.stream().distinct().collect(Collectors.toList()));
                }
            }

        } while (!areMapsEqual(newFirst, currentFirst));

        first = newFirst;
    }

    private List<String> concatenate(List<String> first, List<String> second) {
        List<String> result = new ArrayList<>();
        if (first.isEmpty()) {
            first = new ArrayList<>(Collections.singleton(EPSILON));
        }
        if (second.isEmpty()) {
            second = new ArrayList<>(Collections.singleton(EPSILON));
        }

        for (String firstSymbol : first) {
            for (String secondSymbol : second) {
                String sequence;

                if (isEpsilon(firstSymbol) && isEpsilon(secondSymbol)) {
                    sequence = EPSILON;
                } else if (!isEpsilon(firstSymbol)) {
                    sequence = firstSymbol;
                } else {
                    sequence = secondSymbol;
                }

                result.add(sequence);
            }
        }

        return result;
    }

    private void initializeFollow() {
        Set<String> nonTerminals = grammar.getNonTerminals();
        String startingSymbol = grammar.getStartingSymbol();

        // initialize follow of starting symbol with a singleton list containing EPSILON
        // for all the other non-terminals, initialize their follow with an empty list
        for (String nonTerminal : nonTerminals) {
            if (nonTerminal.equals(startingSymbol)) {
                List<String> singletonEpsilonList = new ArrayList<>(Collections.singleton(EPSILON));
                follow.put(nonTerminal, new ArrayList<>(singletonEpsilonList));
            } else {
                follow.put(nonTerminal, new ArrayList<>());
            }
        }
    }

    private void computeFollow() {
        Map<String, List<String>> current = new HashMap<>();
        Set<String> terminals = grammar.getTerminals();
        Set<String> nonTerminals = grammar.getNonTerminals();
        List<Production> productions = grammar.getProductions();

        for (String symbol : nonTerminals) {
            List<String> currentValue = follow.get(symbol);
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
                        List<String> neighbourFirstValue = first.getOrDefault(valueAfterSymbol, null);
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
