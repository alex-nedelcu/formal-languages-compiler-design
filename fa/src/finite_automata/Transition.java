package finite_automata;

import java.util.List;

public class Transition {

  final String fromState;

  final String through;

  final String toState;

  public Transition(String fromState, String through, String toState) {
    this.fromState = fromState;
    this.through = through;
    this.toState = toState;
  }

  public static Transition of(String stringTransition, String transitionFunctionKey) {
    String sanitized = stringTransition
        .replaceAll(" ", "")
        .replaceAll(transitionFunctionKey, "");

    List<String> tokens = List.of(sanitized.split("="));

    String LHS = tokens.get(0)
        .replaceAll("\\(", "")
        .replaceAll("\\)", "");
    ;
    String RHS = tokens.get(1);

    String fromState = List.of(LHS.split(",")).get(0);
    String through = List.of(LHS.split(",")).get(1);
    String toState = RHS;

    return new Transition(fromState, through, toState);
  }

  @Override
  public String toString() {
    return "Transition{" +
        "fromState='" + fromState + '\'' +
        ", through='" + through + '\'' +
        ", toState='" + toState + '\'' +
        '}';
  }
}
