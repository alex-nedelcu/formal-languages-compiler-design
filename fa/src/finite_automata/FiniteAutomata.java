package finite_automata;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class FiniteAutomata implements IFiniteAutomata {

  public Set<String> states;

  public Set<String> alphabet;

  public Set<String> finalStates;

  public String initialState;

  public List<Transition> transitions;

  @Override
  public void setStates(Set<String> states) {
    this.states = states;
  }

  @Override
  public void setAlphabet(Set<String> alphabet) {
    this.alphabet = alphabet;
  }

  @Override
  public void setFinalStates(Set<String> finalStates) {
    this.finalStates = finalStates;
  }

  @Override
  public void setInitialState(String initialState) {
    this.initialState = initialState;
  }

  @Override
  public void setTransitions(List<Transition> transitions) {
    this.transitions = transitions;
  }

  @Override
  public Set<String> getStates() {
    return states;
  }

  @Override
  public Set<String> getAlphabet() {
    return alphabet;
  }

  @Override
  public Set<String> getFinalStates() {
    return finalStates;
  }

  @Override
  public String getInitialState() {
    return initialState;
  }

  @Override
  public List<Transition> getTransitions() {
    return transitions;
  }

  @Override
  public boolean validateSequence(String sequence) {
    if (finalStates.contains(initialState) && sequence.isEmpty()) {
      return true;
    }

    if (isNullOrEmpty(sequence)) {
      return false;
    }

    String currentState = initialState;

    for (int index = 0; index < sequence.length(); index += 1) {
      String currentSequenceElement = String.valueOf(sequence.charAt(index));
      String nextState = getDestinationStateBySourceStateAndThrough(currentState, currentSequenceElement);

      if (nextState == null) {
        return false;
      }

      currentState = nextState;
    }

    return finalStates.contains(currentState);
  }

  protected String getDestinationStateBySourceStateAndThrough(String sourceState, String through) {
    Optional<Transition> transition = transitions.stream()
        .filter(iterator -> Objects.equals(iterator.fromState, sourceState) && Objects.equals(iterator.through, through))
        .findFirst();

    return transition.map(value -> value.toState).orElse(null);
  }

  private boolean isNullOrEmpty(String string) {
    return string == null || string.trim().isEmpty();
  }

  @Override
  public boolean validateSelf() {
    return areComponentsSet()
        && statesIncludeFinalStates()
        && statesIncludeInitialState()
        && areTransitionsValid()
        && isDeterministic()
        ;
  }

  private boolean isDeterministic() {
    for (Transition first : transitions) {
      for (Transition second : transitions) {
        if (Objects.equals(first.fromState, second.fromState) &&
            Objects.equals(first.through, second.through) &&
            !Objects.equals(first.toState, second.toState)
        ) {
          return false;
        }
      }
    }

    return true;
  }

  private boolean areTransitionsValid() {
    return transitions.stream()
        .map(this::isTransitionValid)
        .filter(isCurrentTransactionValid -> isCurrentTransactionValid == false)
        .toList()
        .isEmpty();
  }

  private boolean isTransitionValid(Transition transition) {
    if (transition == null) {
      return false;
    }

    String fromState = transition.fromState;
    String toState = transition.toState;
    String through = transition.through;

    return states.contains(fromState) && states.contains(toState) && alphabet.contains(through);
  }

  private boolean statesIncludeInitialState() {
    return states.contains(initialState);
  }

  private boolean statesIncludeFinalStates() {
    boolean valid = true;

    for (final String finalState : finalStates) {
      if (!states.contains(finalState)) {
        valid = false;
        break;
      }
    }

    return valid;
  }

  private boolean areComponentsSet() {
    return states != null && states.size() > 0
        && alphabet != null && alphabet.size() > 0
        && finalStates != null && finalStates.size() > 0
        && initialState != null
        && transitions != null && transitions.size() > 0;
  }

  @Override
  public String toString() {
    return "FiniteAutomata{" +
        "states=" + states +
        ", alphabet=" + alphabet +
        ", finalStates=" + finalStates +
        ", initialState='" + initialState + '\'' +
        ", transitions=" + transitions +
        '}';
  }
}
