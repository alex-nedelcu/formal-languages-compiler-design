package parser;

import grammar.IGrammar;

import java.util.List;

public interface IParser {

    List<String> first(IGrammar grammar, String inputSymbol);

}
