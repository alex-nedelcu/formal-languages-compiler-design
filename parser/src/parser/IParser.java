package parser;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IParser {

    Map<String, List<String>> getFirst();

    Map<String, List<String>> getFollow();

    void printParseTable();
}
