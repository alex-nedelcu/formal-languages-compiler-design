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

        // initialize each terminal first with the terminal itself
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

        boolean isModified;
        do {
            isModified = false;
            for (Production production : productions) {
                for (SymbolSequence target : production.targets) {
                    for (String symbol : target.getSymbols()) {
                        var isNullable = false;
                        var sourceFirst = new HashSet<>(first.get(production.getUniqueSource()));
                        ArrayList<String> addition;

                        // Modify nothing if symbol is epsilon
                        if (isEpsilon(symbol)) {
                            addition = new ArrayList<>();
                        } else {
                            // Add ( FIRST(symbol) - EPSILON )
                            // If FIRST(symbol) contains EPSILON, it is nullable
                            addition = new ArrayList<>(first.get(symbol));
                            isNullable = addition.contains(EPSILON);
                            addition.remove(EPSILON);
                        }

                        // If first will be modified
                        if (!sourceFirst.containsAll(addition)) {
                            isModified = true;
                        }
                        sourceFirst.addAll(addition);

                        // Update FIRST
                        first.put(production.getUniqueSource(), sourceFirst.stream().toList());

                        // If a non-nullable step (i.e. FIRST(symbol) does not include EPSILON) was reached, stop.
                        if (!isNullable) {
                            break;
                        }
                    }

                    boolean isNullable = target.getSymbols().stream()
                            .allMatch(symbol -> (nonTerminals.contains(symbol) && first.get(symbol).contains(EPSILON))
                                    || isEpsilon(symbol)
                            );
                    if (isNullable) {
                        var sourceFirst = new HashSet<>(first.get(production.getUniqueSource()));
                        sourceFirst.add(EPSILON);
                        first.put(production.getUniqueSource(), sourceFirst.stream().toList());
                    }
                }
            }
        } while (isModified);
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

        // For each non-terminal
        for (String symbol : nonTerminals) {
            Set<String> currentValue = new HashSet<>(follow.get(symbol));
            Set<Production> matchingProductions = getProductionsWithSymbol(new HashSet<>(productions), symbol);

            // For each production in which the non-terminal is in the RHS
            for (var production : matchingProductions) {
                String leftSide = production.getUniqueSource();
                SymbolSequence rightSide = production.targets.get(0);

                // Get all appearances of current symbol in the current production
                var indexes = new ArrayList<Integer>();
                for (int i = 0; i < rightSide.getSymbols().size(); i++) {
                    if (rightSide.getSymbols().get(i).equals(symbol)) {
                        indexes.add(i);
                    }
                }

                // For each appearance of Symbol in the current Production
                for (int indexOfSymbol : indexes) {
                    boolean isBetaNullable = true;

                    // For each following symbol
                    if (indexOfSymbol < rightSide.getSymbols().size() - 1) {
                        for (int index = indexOfSymbol + 1; index < rightSide.getSymbols().size(); index++) {
                            String nextSymbol = rightSide.getSymbols().get(index);

                            // FOLLOW(SYMBOL) = FOLLOW(SYMBOL) UNION ( FIRST(NEXT_SYMBOL) - EPSILON) )
                            Set<String> neighbourFirstValue = new HashSet<>(first.get(nextSymbol));
                            neighbourFirstValue.remove(EPSILON);
                            neighbourFirstValue.addAll(follow.get(symbol));

                            currentValue.addAll(neighbourFirstValue);

                            // STOP, if NEXT_SYMBOL is not nullable (i.e. its FIRST does not contain EPSILON)
                            if (!first.get(nextSymbol).contains(EPSILON)) {
                                isBetaNullable = false;
                                break;
                            }
                        }
                    }

                    boolean isBetaNull = indexOfSymbol == rightSide.getSymbols().size() - 1;
                    if (isBetaNull || isBetaNullable) {
                        currentValue.addAll(follow.get(leftSide));
                    }
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

    public void parseTable() {
        var parseTable = new HashMap<String, HashMap<String, ArrayList<String>>>();
        for (String nonTerminal : grammar.getNonTerminals()) {
            var newEntry = new HashMap<String, ArrayList<String>>();
            for (String terminal : grammar.getTerminals()) {
                newEntry.put(terminal, new ArrayList<>());
                parseTable.put(nonTerminal, newEntry);
            }
        }

        int counter = 0;
        for (int productionIndex = 0; productionIndex < grammar.getProductions().size(); productionIndex++) {
            var production = grammar.getProductions().get(productionIndex);
            for (int index = 0; index < production.targets.size(); index++) {
                counter++;
                var firstAlpha = new ArrayList<String>();
                var rhs = production.targets.get(index);
                boolean isNullable = true;
                for (var symbol : rhs.getSymbols()) {
                    System.out.println(symbol);

                    ArrayList<String> symbolFirst;
                    if (isEpsilon(symbol)) {
                        symbolFirst = new ArrayList<>(List.of(EPSILON));
                    } else {
                        symbolFirst = new ArrayList<>(first.get(symbol));
                    }
                    boolean containsEpsilon = symbolFirst.contains(EPSILON);
                    symbolFirst.remove(EPSILON);
                    firstAlpha.addAll(symbolFirst);

                    if (!containsEpsilon) {
                        isNullable = false;
                        break;
                    }
                }

                for (var b : firstAlpha) {
                    var parseTableA = parseTable.get(production.getUniqueSource());
                    var parseTableAB = new HashSet<>(parseTableA.get(b));
                    parseTableAB.add(String.valueOf(counter));
                    parseTableA.put(b, new ArrayList<>(parseTableAB.stream().toList()));
                }

                if (isNullable || isEpsilon(rhs.getSymbols().toString())) {
                    for (var c : follow.get(production.getUniqueSource())) {
                        var parseTableA = parseTable.get(production.getUniqueSource());
                        var parseTableAB = new HashSet<>(parseTableA.get(c));
                        parseTableAB.add(String.valueOf(counter));
                        parseTableA.put(c, new ArrayList<>(parseTableAB.stream().toList()));
                    }
                }
            }
        }

        System.out.print("       ");
        for (String terminal : grammar.getTerminals()) {
            System.out.print(terminal);
            System.out.print("     ");
        }
        System.out.println();
        System.out.println("     ------------------------------------------------");
        for (String nonTerminal : grammar.getNonTerminals()) {
            System.out.print(nonTerminal);
            System.out.print("   | ");
            for (String terminal : grammar.getTerminals()) {
                var temp = parseTable.get(nonTerminal).get(terminal);
                if (temp.isEmpty()) {
                    System.out.print(" X    ");
                } else {
                    System.out.print(temp);
                    System.out.print("   ");
                }
            }
            System.out.println();
        }
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
