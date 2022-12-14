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

        for (String terminal : terminals) {
            first.put(terminal, List.of(terminal));
        }

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
        Set<String> nonTerminals = grammar.getNonTerminals();
        List<Production> productions = grammar.getProductions();

        boolean added = false;
        do {
            added = false;
            for (Production production : productions) {
                for (SymbolSequence target : production.targets) {
                    for (String symbol : target.getSymbols()) {
                        var containsEpsilon = false;
                        var temp = new HashSet<>(first.get(production.getUniqueSource()));
                        ArrayList<String> addition;
                        if (isEpsilon(symbol)) {
                            addition = new ArrayList<>();
                        } else {
                            addition = new ArrayList<>(first.get(symbol));
                            containsEpsilon = addition.contains(EPSILON);
                            addition.remove(EPSILON);
                        }
                        if (!temp.containsAll(addition)) {
                            added = true;
                        }
                        temp.addAll(addition);
                        first.put(production.getUniqueSource(), temp.stream().toList());
                        if (!containsEpsilon) {
                            break;
                        }
                    }
                    if (target.getSymbols().contains(EPSILON) || target.getSymbols().stream().allMatch(x -> (nonTerminals.contains(x) && first.get(x).contains(EPSILON)) || isEpsilon(x))) {
                        var temp = new HashSet<>(first.get(production.getUniqueSource()));
                        temp.add(EPSILON);
                        first.put(production.getUniqueSource(), temp.stream().toList());
                    }
                }
            }
        } while (added);
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
        Map<String, List<String>> current = new HashMap<>(follow);
        Set<String> nonTerminals = grammar.getNonTerminals();
        List<Production> productions = grammar.getProductions();

        for (String symbol : nonTerminals) {
            Set<String> currentValue = new HashSet<>(follow.get(symbol));
            Set<Production> matchingProductions = getProductionsWithSymbol(new HashSet<>(productions), symbol);

            for (var production : matchingProductions) {
                String leftSide = production.getUniqueSource();
                SymbolSequence rightSide = production.targets.get(0);

                int indexOfSymbol = rightSide.getSymbols().indexOf(symbol);
                if (indexOfSymbol < rightSide.getSymbols().size() - 1) {
                    boolean isBroken = false;
                    for (int index = indexOfSymbol + 1; index < rightSide.getSymbols().size(); index++) {
                        String nextSymbol = rightSide.getSymbols().get(index);
                        Set<String> neighbourFirstValue = new HashSet<>(first.get(nextSymbol));
                        neighbourFirstValue.remove(EPSILON);
                        neighbourFirstValue.addAll(follow.get(symbol));
                        currentValue.addAll(neighbourFirstValue);
                        if (!first.get(nextSymbol).contains(EPSILON)) {
                            isBroken = true;
                            break;
                        }
                    }
                    if (!isBroken) {
                        currentValue.addAll(follow.get(leftSide));
                        currentValue.add(EPSILON);
                    }
                }
                if (indexOfSymbol == rightSide.getSymbols().size() - 1) {
                    currentValue.addAll(follow.get(leftSide));
                }
            }

            follow.put(symbol, currentValue.stream().toList());
        }

        if (!areMapsEqual(follow, current)) {
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
        Map<String, List<String>> temp = new HashMap<>();
        for (var nonTerminal : grammar.getNonTerminals()) {
            temp.put(nonTerminal, first.get(nonTerminal));
        }
        return temp;
    }

    @Override
    public Map<String, List<String>> getFollow() {
        Map<String, List<String>> temp = new HashMap<>();
        for (var nonTerminal : grammar.getNonTerminals()) {
            temp.put(nonTerminal, follow.get(nonTerminal));
        }
        return temp;
    }
}
