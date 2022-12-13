import grammar.Grammar;
import grammar.IGrammar;
import parser.IParser;
import parser.Parser;

import java.util.Scanner;

public final class Main {
    // LL(1)
    public static void main(String[] args) {
        IGrammar grammar = new Grammar("/Users/alexandru.nedelcu.ext/Desktop/Other/Uni/FLCD/formal-languages-compiler-design/parser/src/data/g1.txt");
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

                default -> System.out.println("Invalid option!");
            }

            System.out.println();
        }

    }
}
