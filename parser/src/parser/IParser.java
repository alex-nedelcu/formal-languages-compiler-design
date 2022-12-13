package parser;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IParser {

    Map<String, Set<String>> getFirst();

    Map<String, Set<String>> getFollow();

}
