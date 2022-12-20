import grammar.Grammar;
import grammar.IGrammar;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import parser.IParser;
import parser.Parser;

public final class Main {

  // LL(1)
  public static void main(String[] args) throws IOException {
    String DATA_BASE_PATH = "/Users/alexandru.nedelcu.ext/Desktop/Other/Uni/FLCD/formal-languages-compiler-design/parser/src/data";
    String GRAMMAR = "3";
    String GRAMMAR_FILE = DATA_BASE_PATH + "/g" + GRAMMAR + ".txt";
    String OUTPUT_FILE = DATA_BASE_PATH + "/out" + GRAMMAR + ".txt";

    IGrammar grammar = new Grammar(GRAMMAR_FILE);
    grammar.process();
    IParser parser = new Parser(grammar);

    boolean stop = false;

    Scanner keyboard = new Scanner(System.in);

    while (!stop) {
      System.out.println("Choose option:");
      System.out.println("[0] Exit");
      System.out.println("[1] Non-terminals");
      System.out.println("[2] Terminals");
      System.out.println("[3] Productions");
      System.out.println("[4] Productions for a given non-terminal");
      System.out.println("[5] Perform CFG check");
      System.out.println("[6] Get FIRST");
      System.out.println("[7] Get FOLLOW");
      System.out.println("[8] Parse table");
      System.out.println("[9] Derivations string for sequence");
      System.out.print("> ");

      String option = keyboard.nextLine().trim();

      switch (option) {
        case "0" -> stop = true;
        case "1" -> System.out.println(grammar.getNonTerminals());
        case "2" -> System.out.println(grammar.getTerminals());
        case "3" -> System.out.println(grammar.getProductions());
        case "4" -> {
          System.out.println("Non-terminal: ");
          String nonTerminal = keyboard.nextLine().trim();

          System.out.println("Productions for non-terminal " + nonTerminal + ": ");
          System.out.println(grammar.getProductionsByNonTerminal(nonTerminal));
        }
        case "5" -> System.out.println(grammar.isContextFree());
        case "6" -> System.out.println(parser.getFirst());
        case "7" -> System.out.println(parser.getFollow());
        case "8" -> parser.printParseTable();
        case "9" -> {
          // accepted sequence: abbc, should output 1223 (see Seminar 9 notes)
          System.out.println("Sequence: ");
          String sequence = keyboard.nextLine().trim();

          System.out.println(parser.getDerivationsStringForSequence(sequence));

          System.out.println("\nPrint parser output? (y/n) ");
          String answer = keyboard.nextLine().trim();

          if ("y".equals(answer)) {
            System.out.println(parser.getParserOutput());
          }

          File outputFile = new File(OUTPUT_FILE);
          outputFile.createNewFile();

          try (FileWriter writer = new FileWriter(OUTPUT_FILE)) {
            writer.write(parser.getParserOutput().toString());
          } catch (IOException ioException) {
            System.out.println("Error while writing to file: " + ioException.getMessage());
          }
        }

        default -> System.out.println("Invalid option!");
      }
      System.out.println();
    }
  }
}
