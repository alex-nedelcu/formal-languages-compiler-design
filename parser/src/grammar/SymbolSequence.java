package grammar;

import java.util.Arrays;
import java.util.List;

public class SymbolSequence {

  private final List<String> symbols;

  public SymbolSequence(String dottedSequence) {
    this.symbols = separateSymbols(dottedSequence);
  }

  public List<String> getSymbols() {
    return symbols;
  }

  private List<String> separateSymbols(String dottedSequence) {
    // receives 'a.B.ANOTHER_SYMBOL' and returns ['a', 'B', 'ANOTHER_SYMBOL']
    return Arrays.stream(dottedSequence.split("\\.")).map(String::trim).toList();
  }

  @Override
  public String toString() {
    return "SymbolSequence{" +
        "symbols=" + symbols +
        '}';
  }
}
