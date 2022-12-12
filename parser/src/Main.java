import grammar.Grammar;
import grammar.IGrammar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    // LL(1)
    // only algorithm for FIRST and FOLLOW
    public static void main(String[] args) {
        IGrammar grammar = new Grammar("/Users/alexandru.nedelcu.ext/Desktop/Other/Uni/FLCD/formal-languages-compiler-design/parser/src/data/g2.txt");
        grammar.process();

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

                default -> System.out.println("Invalid option!");
            }

            System.out.println();
        }

    }
}
