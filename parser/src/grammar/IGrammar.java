package grammar;

import java.util.List;
import java.util.Set;

public interface IGrammar {

  void process();

  Set<String> getNonTerminals();

  Set<String> getTerminals();

  List<Production> getProductions();

  List<Production> getIndexedProductions();

  List<Production> getProductionsByNonTerminal(String nonTerminal);

  String getStartingSymbol();

  boolean isContextFree();
}
