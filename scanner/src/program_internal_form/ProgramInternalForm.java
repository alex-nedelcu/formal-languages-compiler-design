package program_internal_form;

import java.util.ArrayList;
import java.util.List;

public class ProgramInternalForm implements IProgramInternalForm {

  private final List<Pair> elements;

  public ProgramInternalForm() {
    elements = new ArrayList<>();
  }

  @Override
  public int add(Pair pair) {
    var addedOnPosition = elements.size();
    elements.add(pair);
    return addedOnPosition;
  }

  @Override
  public String toString() {
    return elements.stream()
        .map(Pair::toString)
        .reduce("", (accumulator, current) -> accumulator + "\n" + current);
  }
}
