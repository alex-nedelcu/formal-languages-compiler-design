package parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ParserOutput {

  List<Stack<String>> inputStackStates;
  List<Stack<String>> workingStackStates;
  List<List<String>> outputBandStates;
  List<List<String>> stackTopsStates;
  List<String> actions;

  public ParserOutput() {
    this.inputStackStates = new ArrayList<>();
    this.workingStackStates = new ArrayList<>();
    this.outputBandStates = new ArrayList<>();
    this.stackTopsStates = new ArrayList<>();
    this.actions = new ArrayList<>();
  }

  @Override
  public String toString() {
    return "ParserOutput" + "\n" + "{" + "\n" +
        "  inputStackStates=" + inputStackStates + "\n" +
        "  workingStackStates=" + workingStackStates + "\n" +
        "  outputBandStates=" + outputBandStates + "\n" +
        "  stackTopsStates=" + stackTopsStates + "\n" +
        "  actions=" + actions + "\n" +
        '}';
  }
}
