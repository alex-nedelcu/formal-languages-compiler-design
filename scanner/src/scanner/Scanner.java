package scanner;

import classifier.ClassifiedTokens;
import classifier.Classifier;
import classifier.TokenType;
import exceptions.LexicalException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import program_internal_form.IProgramInternalForm;
import program_internal_form.Pair;
import symbol_table.ISymbolTable;
import utils.Utility;

public class Scanner implements IScanner {

  static final List<String> CRITICAL_TOKENS = List.of("+", "*", "{", "}", "(", ")", "[", "]");

  static final String IDENTIFIER_CODIFICATION = "id";
  static final String CONSTANT_CODIFICATION = "const";
  static final int DEFAULT_POSITION_NON_IDENTIFIERS_OR_CONSTANTS = -1;

  private final ISymbolTable identifiersSymbolTable;
  private final ISymbolTable constantsSymbolTable;
  private final IProgramInternalForm PIF;
  private final List<String> inputTokens;
  private final ClassifiedTokens classifiedTokens;

  public Scanner(ISymbolTable identifiersSymbolTable, ISymbolTable constantsSymbolTable, IProgramInternalForm PIF, String rulesFile) {
    this.identifiersSymbolTable = identifiersSymbolTable;
    this.constantsSymbolTable = constantsSymbolTable;
    this.PIF = PIF;
    this.inputTokens = getFileContentByPath(rulesFile);
    this.classifiedTokens = new ClassifiedTokens(inputTokens);
  }

  @Override
  public ScanResult scan(String programSourceCodePath) {
    List<String> lines = prepareFileLinesContentForTokenize(getFileContentByPath(programSourceCodePath));
    AtomicInteger lineCounter = new AtomicInteger();

    System.out.println(">>> CLASSIFICATION <<<\n");

    lines.forEach(line -> {
      lineCounter.addAndGet(1);

      List<String> tokens = splitIntoTokens(line);

      tokens.forEach(token -> {
        TokenType type = Classifier.classify(token, classifiedTokens);

        System.out.println(type + "   " + token);

        switch (type) {

          case UNKNOWN -> throw new LexicalException(
              "Lexical error: could not classify token " + token + " on line " + lineCounter.get()
          );

          case IDENTIFIER -> {
            identifiersSymbolTable.add(token);
            PIF.add(new Pair(IDENTIFIER_CODIFICATION, identifiersSymbolTable.search(token)));
          }

          case CONSTANT -> {
            constantsSymbolTable.add(token);
            PIF.add(new Pair(CONSTANT_CODIFICATION, constantsSymbolTable.search(token)));
          }

          default -> PIF.add(new Pair(token, DEFAULT_POSITION_NON_IDENTIFIERS_OR_CONSTANTS));
        }
      });
    });

    return new ScanResult(identifiersSymbolTable, constantsSymbolTable, PIF);
  }

  private List<String> splitIntoTokens(String line) {
    if (Utility.isNullOrEmpty(line)) {
      return new ArrayList<>();
    }

    List<String> tokens = Arrays.asList(line.split(" "));
    return reconstructAggregatedOperators(
        tokens.stream()
            .filter(token -> token.length() > 0)
            .collect(Collectors.toList())
    );
  }

  private List<String> getFileContentByPath(String path) {
    try {
      return Files.readAllLines(Path.of(path));
    } catch (IOException ioException) {
      throw new RuntimeException("Error reading the file " + path);
    }
  }

  private List<String> prepareFileLinesContentForTokenize(List<String> fileContent) {
    List<String> cleaned = fileContent.stream()
        .map(String::trim)
        .map(this::prepareLineForTokenize)
        .toList();

    return cleaned;
  }

  private String prepareLineForTokenize(String line) {
    if (Utility.isNullOrEmpty(line)) {
      return "";
    }

    for (String inputToken : inputTokens) {
      if (!classifiedTokens.getExcluded().contains(inputToken) && CRITICAL_TOKENS.contains(inputToken)) {
        inputToken = "\\" + inputToken;
      }

      if (!classifiedTokens.getKeywords().contains(inputToken)) {
        line = line.replaceAll(inputToken, " " + inputToken + " ");
      }
    }

    return line;
  }

  private List<String> reconstructAggregatedOperators(List<String> tokens) {
    List<String> partialAggregateOperators = List.of("!", ">", "<", "=");

    if (tokens.size() < 2) {
      return tokens;
    }

    List<String> reconstructed = new ArrayList<>();

    int current = 1;

    while (current < tokens.size()) {
      int previous = current - 1;

      String previousChar = tokens.get(previous);
      String currentChar = tokens.get(current);

      if (Objects.equals(currentChar, "=") && partialAggregateOperators.contains(previousChar)) {
        reconstructed.add(previousChar + currentChar);
        current += 1;
      } else {
        reconstructed.add(previousChar);
      }

      current += 1;
      if (current == tokens.size()) {
        reconstructed.add(tokens.get(current - 1));
      }
    }

    return reconstructed;
  }
}
