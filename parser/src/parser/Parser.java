package parser;

import grammar.IGrammar;
import grammar.Production;
import grammar.SymbolSequence;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Pattern;

public class Parser implements IParser {

  private static final String EPSILON = "EPSILON";
  private static final String EMPTY_STACK_MARK = "$";
  private static final String ACC = "acc";
  private static final String POP = "pop";
  private static final String ERR = "err";
  private final IGrammar grammar;
  private Map<String, List<String>> first;
  private Map<String, List<String>> follow;
  private Map<String, Map<String, List<String>>> parseTable; // e.g. A: {b: [], +: []}
  private ParserOutput parserOutput;
  private List<String> parseTableConflicts;

  public Parser(IGrammar grammar) {
    this.grammar = grammar;
    assert (this.grammar.isContextFree());

    this.first = new HashMap<>();
    initializeFirst();
    computeFirst();

    this.follow = new HashMap<>();
    initializeFollow();
    computeFollow();

    this.parseTable = new HashMap<>();
    this.parseTableConflicts = new ArrayList<>();
    computeParseTable();
  }

  @Override
  public ParserOutput getParserOutput() {
    return parserOutput;
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

  private void computeParseTable() {
    Set<String> terminals = grammar.getTerminals();
    Set<String> nonTerminals = grammar.getNonTerminals();
    Set<String> rowKeys = extractRowKeysFromGrammar(grammar);
    Set<String> columnKeys = extractColumnKeysFromGrammar(grammar);

    for (String rowKey : rowKeys) {
      for (String columnKey : columnKeys) {
        boolean set = false;
        Map<String, List<String>> previousMapping = parseTable.get(rowKey);
        List<Production> indexedProductions = grammar.getIndexedProductions();
        List<Production> indexedProductionBySource = indexedProductions
            .stream()
            .filter(production -> production.getUniqueSource().equals(rowKey))
            .toList();

        // parseTable(A, a) = (alpha, i), if
        // A = non-terminal, a = terminal, a != EPSILON, A -> alpha production , a belongs to FIRST(alpha)
        if (nonTerminals.contains(rowKey) && terminals.contains(columnKey) && !columnKey.equals(EPSILON)) {
          for (Production production : indexedProductionBySource) {
            // alpha
            SymbolSequence target = production.getUniqueTargetForIndexedProduction();

            // FIRST(alpha)
            List<String> targetSequenceFirst = computeFirstForSequence(target);

            // a belongs to FIRST(alpha)
            if (targetSequenceFirst.contains(columnKey)) {
              String action = production.index.toString();
              Map<String, List<String>> updatedMapping = addActionToMapping(previousMapping, columnKey, rowKey, action);
              parseTable.put(rowKey, updatedMapping);
              set = true;
            }
          }
        }

        // parseTable(A, b) = (alpha, i), if
        // A = non-terminal, A -> alpha production, EPSILON belongs to FIRST(alpha), b belongs to FOLLOW(A)
        if (nonTerminals.contains(rowKey) && follow.get(rowKey).contains(columnKey)) {
          for (Production production : indexedProductionBySource) {
            // alpha
            SymbolSequence target = production.getUniqueTargetForIndexedProduction();

            // FIRST(alpha)
            List<String> targetSequenceFirst = computeFirstForSequence(target);

            // EPSILON belongs to FIRST(alpha)
            if (targetSequenceFirst.contains(EPSILON)) {
              String action = production.index.toString();
              Map<String, List<String>> updatedMapping = addActionToMapping(previousMapping, columnKey, rowKey, action);
              parseTable.put(rowKey, updatedMapping);
              set = true;
            }
          }
        }

        // parseTable(a, a) = pop, if a = terminal
        if (rowKey.equals(columnKey) && terminals.contains(rowKey)) {
          Map<String, List<String>> updatedMapping = addActionToMapping(previousMapping, columnKey, rowKey, POP);
          parseTable.put(rowKey, updatedMapping);
          set = true;
        }

        // parseTable($, $) = acc
        if (rowKey.equals(EMPTY_STACK_MARK) && columnKey.equals(EMPTY_STACK_MARK)) {
          Map<String, List<String>> updatedMapping = addActionToMapping(previousMapping, columnKey, rowKey, ACC);
          parseTable.put(rowKey, updatedMapping);
          set = true;
        }

        // default
        if (!set) {
          Map<String, List<String>> updatedMapping = addActionToMapping(previousMapping, columnKey, rowKey, ERR);
          parseTable.put(rowKey, updatedMapping);
        }
      }
    }
  }

  private List<String> computeFirstForSequence(SymbolSequence symbols) {
    String symbolOnFirstPosition = symbols.getSymbols().get(0);
    List<String> sequenceFirst = first.get(symbolOnFirstPosition);

    if (isEpsilon(symbolOnFirstPosition)) {
      sequenceFirst = new ArrayList<>(List.of(EPSILON));
    }

    for (int index = 1; index < symbols.getSymbols().size(); index += 1) {
      String currentSymbol = symbols.getSymbols().get(index);
      List<String> currentSymbolFirst = first.get(currentSymbol);

      sequenceFirst = concatenate(sequenceFirst, currentSymbolFirst);
    }

    return sequenceFirst;
  }

  private Map<String, List<String>> addActionToMapping(Map<String, List<String>> mapping, String columnKey, String rowKey, String action) {
    // if mapping is null creates and returns a new mapping {columnKey: [action]}
    // else it merges current mapping with {columnKey: [action]}
    Map<String, List<String>> newMapping = mapping;
    List<String> newActionList = new ArrayList<>(List.of(action));

    if (newMapping == null) {
      newMapping = new HashMap<>();
    }

    List<String> previousActionList = newMapping.get(columnKey);
    if (previousActionList != null) {
      newActionList.addAll(previousActionList);
    }

    if (newActionList.size() > 1) {
      parseTableConflicts.add("Conflict (columnKey=" + columnKey + ", rowKey=" + rowKey + ", actions=" + newActionList + ")");
    }

    newMapping.put(columnKey, newActionList);

    return newMapping;
  }

  Set<String> extractRowKeysFromGrammar(IGrammar grammar) {
    Set<String> rowKeys = new HashSet<>();
    rowKeys.addAll(grammar.getNonTerminals());
    rowKeys.addAll(grammar.getTerminals());
    rowKeys.add(EMPTY_STACK_MARK);

    return rowKeys;
  }

  private Set<String> extractColumnKeysFromGrammar(IGrammar grammar) {
    Set<String> columnKeys = new HashSet<>();
    columnKeys.addAll(grammar.getTerminals());
    columnKeys.add(EMPTY_STACK_MARK);

    return columnKeys;
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

  @Override
  public void printParseTable() {
    System.out.println("CONFLICTS");
    parseTableConflicts.forEach(System.out::println);
    System.out.println();

    String tenWhitespaces = "          ";

    Set<String> columnKeys = extractColumnKeysFromGrammar(grammar);
    Set<String> rowKeys = extractRowKeysFromGrammar(grammar);

    for (String columnKey : columnKeys) {
      System.out.print(tenWhitespaces + columnKey);
    }

    System.out.println();

    for (String rowKey : rowKeys) {
      if (grammar.getNonTerminals().contains(rowKey)) {
        System.out.print(rowKey);

        for (String columnKey : columnKeys) {
          List<String> actions = parseTable.get(rowKey).get(columnKey);
          System.out.printf("%10s ", actions);
        }

        System.out.println();
      }
    }
  }

  @Override
  public List<String> getDerivationsStringForSequence(String sequence) {
    ParserOutput output = new ParserOutput();
    boolean accepted = false;
    List<Production> indexedProductions = grammar.getIndexedProductions();

    Stack<String> inputStack = new Stack<>(); // alpha
    Stack<String> workingStack = new Stack<>(); // beta
    List<String> outputBand = new ArrayList<>(); // pi

    // Initialize input stack
    inputStack.push(EMPTY_STACK_MARK);
    for (int index = sequence.length() - 1; index >= 0; index -= 1) {
      char current = sequence.charAt(index);
      inputStack.push(String.valueOf(current));
    }

    // Initialize working stack
    workingStack.push(EMPTY_STACK_MARK);
    workingStack.push(grammar.getStartingSymbol());

    boolean go = true;

    while (go) {
      output.inputStackStates.add((Stack<String>) inputStack.clone());
      output.workingStackStates.add((Stack<String>) workingStack.clone());
      output.outputBandStates.add(new ArrayList<>(outputBand));

      String workingStackTop = workingStack.peek();
      String inputStackTop = inputStack.peek();

      output.stackTopsStates.add(List.of(workingStackTop, inputStackTop));

      List<String> action = parseTable.get(workingStackTop).get(inputStackTop);

      // Action Push
      if (isProductionIndex(action)) {
        output.actions.add("push");

        Integer index = Integer.valueOf(action.get(0));
        Production productionWithIndex = indexedProductions
            .stream()
            .filter(production -> Objects.equals(production.index, index))
            .findFirst()
            .orElseThrow();

        // Pop top of working stack
        workingStack.pop();

        // Push rhs of production to working stack
        SymbolSequence rhs = productionWithIndex.getUniqueTargetForIndexedProduction();
        for (int symbolIndex = rhs.getSymbols().size() - 1; symbolIndex >= 0; symbolIndex -= 1) {
          String symbol = rhs.getSymbols().get(symbolIndex);
          if (!isEpsilon(symbol)) {
            workingStack.push(symbol);
          }
        }

        // Add production index to output band
        outputBand.add(index.toString());
      } else {
        // Action Pop
        if (isPop(action)) {
          output.actions.add("pop");

          // Pop top of input stack
          inputStack.pop();

          // Pop top of working stack
          workingStack.pop();
        } else {
          if (isAcc(action)) {
            output.actions.add("acc");
            accepted = true;
          }

          go = false;
        }
      }
    }

    System.out.println("Sequence is " + (accepted ? "accepted" : "not accepted"));

    if (outputBand.isEmpty() && accepted) {
      outputBand.add(EPSILON);
    }

    if (!accepted) {
      output.actions.add("err");
      outputBand = new ArrayList<>(List.of(ERR));
    }

    parserOutput = output;
    return outputBand;
  }

  private boolean isProductionIndex(List<String> actions) {
    Pattern numericPattern = Pattern.compile("-?\\d+(\\.\\d+)?");
    return actions != null && actions.size() == 1 && numericPattern.matcher(actions.get(0)).matches();
  }

  private boolean isPop(List<String> actions) {
    return actions != null && actions.size() == 1 && actions.get(0).equals(POP);
  }

  private boolean isAcc(List<String> actions) {
    return actions != null && actions.size() == 1 && actions.get(0).equals(ACC);
  }
}
