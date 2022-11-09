package finite_automata;

import java.util.List;
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
    public boolean validateSelf() {
        return areComponentsSet()
                && statesIncludeFinalStates()
                && statesIncludeInitialState()
                && areTransitionsValid();
    }

    private boolean areTransitionsValid() {
        return transitions.stream()
                .map(this::isTransitionValid)
                .filter(isCurrentTransactionValid -> isCurrentTransactionValid == false)
                .toList()
                .size() == 0;
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
