import finite_automata.FiniteAutomataParser;
import finite_automata.IFiniteAutomata;
import java.util.Scanner;

public class Main {

  public static void main(String[] args) {
    IFiniteAutomata finiteAutomata = new FiniteAutomataParser("/Users/alexandru.nedelcu.ext/Desktop/Other/Uni/FLCD/formal-languages-compiler-design/fa/src/data/fa.in").parse();

    boolean stop = false;

    Scanner keyboard = new Scanner(System.in);

    while (!stop) {
      System.out.println("Choose option:");
      System.out.println("[0] Exit");
      System.out.println("[1] Display set of states");
      System.out.println("[2] Display alphabet");
      System.out.println("[3] Display transitions");
      System.out.println("[4] Display initial state");
      System.out.println("[5] Display final states");
      System.out.println("[6] Validate sequence");
      System.out.print("> ");

      String option = keyboard.nextLine().trim();

      switch (option) {
        case "0" -> stop = true;
        case "1" -> System.out.println(finiteAutomata.getStates());
        case "2" -> System.out.println(finiteAutomata.getAlphabet());
        case "3" -> System.out.println(finiteAutomata.getTransitions());
        case "4" -> System.out.println(finiteAutomata.getInitialState());
        case "5" -> System.out.println(finiteAutomata.getFinalStates());
        case "6" -> {
          System.out.println("Introduce sequence: ");
          String sequence = keyboard.nextLine().trim();
          boolean isValid = finiteAutomata.validateSequence(sequence);

          System.out.println(isValid ? "Sequence is valid!" : "Sequence is NOT valid!");
        }
        default -> System.out.println("Invalid option!");
      }

      System.out.println();
    }
  }
}
