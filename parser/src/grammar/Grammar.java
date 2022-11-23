package grammar;

import exceptions.GrammarException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Grammar implements IGrammar {

    private static final String NON_TERMINALS_KEY = "NON-TERMINALS";
    private static final String TERMINALS_KEY = "TERMINALS";
    private static final String STARTING_SYMBOL_KEY = "START";
    private static final String PRODUCTIONS_KEY = "PRODUCTIONS";

    private Set<String> nonTerminals;
    private Set<String> terminals;
    private List<Production> productions;


    private String startingSymbol;
    public String filename;

    public Grammar(String filename) {
        this.filename = filename;
        this.nonTerminals = new HashSet<>();
        this.terminals = new HashSet<>();
        this.productions = new ArrayList<>();
    }

    @Override
    public void process() {
        if (filename == null) {
            throw new GrammarException("You must provide the file that contains the grammar definition!");
        }

        List<String> lines = getFileContentByPath(filename);

        lines.forEach(line -> {
            List<String> lineTokens = List.of(line.split(":"));

            if (lineTokens.size() != 2) {
                throw new GrammarException("Grammar file format is invalid!");
            }

            String key = lineTokens.get(0).trim();
            String value = lineTokens.get(1).trim();

            switch (key) {
                case NON_TERMINALS_KEY -> nonTerminals = parseNonTerminalsLine(value);
                case TERMINALS_KEY -> terminals = parseTerminalsLine(value);
                case STARTING_SYMBOL_KEY -> startingSymbol = parseStartingSymbolLine(value);
                case PRODUCTIONS_KEY -> productions = parseProductionsLine(value);
                default -> throw new GrammarException("Invalid key " + key);
            }
        });
    }

    private Set<String> parseNonTerminalsLine(String value) {
        // e.g. value: S A B
        return Stream.of(value.split(" ")).map(String::trim).collect(Collectors.toSet());
    }

    private Set<String> parseTerminalsLine(String value) {
        // e.g. value: a b +
        return Stream.of(value.split(" ")).map(String::trim).collect(Collectors.toSet());
    }

    private String parseStartingSymbolLine(String value) {
        return value.trim();
    }

    private List<Production> parseProductionsLine(String value) {
        return Stream.of(value.split(";"))
            .map(Production::new)
            .collect(Collectors.toList());
    }

    private List<String> getFileContentByPath(String filename) {
        try {
            return Files.readAllLines(Path.of(filename));
        } catch (IOException ignored) {
            throw new GrammarException("Error reading the file " + filename);
        }
    }

    private String removeAllCharactersFromString(String string, String character) {
        return string.replaceAll(character, "");
    }

    @Override
    public Set<String> getNonTerminals() {
        return nonTerminals;
    }

    @Override
    public Set<String> getTerminals() {
        return terminals;
    }

    @Override
    public List<Production> getProductions() {
        return productions;
    }

    private String getStartingSymbol() {
        return startingSymbol;
    }

    @Override
    public List<Production> getProductionsByNonTerminal(String nonTerminal) {
        return productions
            .stream()
            .filter(production -> production.source.equals(nonTerminal))
            .collect(Collectors.toList());
    }
}
