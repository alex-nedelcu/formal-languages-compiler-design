package parser;

import java.util.List;
import java.util.Map;

public interface IParser {

  Map<String, List<String>> getFirst();

  Map<String, List<String>> getFollow();

  void printParseTable();

  List<String> getDerivationsStringForSequence(String sequence);
}
