package specialized_finite_automata.identifier;

import finite_automata.FiniteAutomata;
import finite_automata.FiniteAutomataParser;
import finite_automata.IFiniteAutomata;

public class IdentifierFiniteAutomata extends FiniteAutomata {

  private static final String filename = "/Users/alexandru.nedelcu.ext/Desktop/Other/Uni/FLCD/formal-languages-compiler-design/scanner/src/specialized_finite_automata/identifier/identifier_finite_automata.txt";

  public IdentifierFiniteAutomata() {
    IFiniteAutomata finiteAutomata = new FiniteAutomataParser(filename).parse();
    super.setStates(finiteAutomata.getStates());
    super.setAlphabet(finiteAutomata.getAlphabet());
    super.setInitialState(finiteAutomata.getInitialState());
    super.setFinalStates(finiteAutomata.getFinalStates());
    super.setTransitions(finiteAutomata.getTransitions());
  }

  @Override
  public boolean validateSequence(String sequence) {
    return super.validateSequence(codifySequence(sequence));
  }

  private String codifySequence(String sequence) {
    StringBuilder codified = new StringBuilder();

    for (int index = 0; index < sequence.length(); index += 1) {
      String current = String.valueOf(sequence.charAt(index));
      codified.append(IdentifierMappings.getMappingFor(current));
    }

    return codified.toString();
  }
}
