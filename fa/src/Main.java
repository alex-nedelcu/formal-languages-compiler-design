import finite_automata.FiniteAutomataParser;
import finite_automata.IFiniteAutomata;

public class Main {
    public static void main(String[] args) {

        // MENU
        // 1. display finite automata components
        // 2. check sequence

        IFiniteAutomata finiteAutomata = new FiniteAutomataParser("/Users/alexnedelcu/Desktop/Uni/FLCD/formal-languages-compiler-design/fa/src/data/fa.in").parse();

        System.out.println(finiteAutomata);

    }
}
