package finite_automata;

import java.util.List;
import java.util.Set;

public interface IFiniteAutomata {

    void setStates(Set<String> states);

    void setAlphabet(Set<String> alphabet);

    void setFinalStates(Set<String> finalStates);

    void setInitialState(String initialState);

    void setTransitions(List<Transition> transitions);

    boolean validateSelf();
}
