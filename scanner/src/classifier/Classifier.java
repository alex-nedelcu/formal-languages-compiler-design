package classifier;

import java.util.List;
import specialized_finite_automata.identifier.IdentifierFiniteAutomata;
import specialized_finite_automata.integer.IntegerFiniteAutomata;

public class Classifier {

  public static TokenType classify(String token, ClassifiedTokens classifiedTokens) {
    if (matchesKeyword(token, classifiedTokens.getKeywords())) {
      return TokenType.KEYWORD;
    } else if (matchesOperator(token, classifiedTokens.getOperators())) {
      return TokenType.OPERATOR;
    } else if (matchesSeparator(token, classifiedTokens.getSeparators())) {
      return TokenType.SEPARATOR;
    } else if (matchesIdentifierByFiniteAutomata(token)) {
      return TokenType.IDENTIFIER;
    } else if (matchesConstantByFiniteAutomata(token)) {
      return TokenType.CONSTANT;
    } else {
      return TokenType.UNKNOWN;
    }
  }

  private static boolean matchesKeyword(String token, List<String> keywords) {
    return keywords.contains(token);
  }

  private static boolean matchesOperator(String token, List<String> operators) {
    return operators.contains(token);
  }

  private static boolean matchesSeparator(String token, List<String> separators) {
    return separators.contains(token);
  }

  /**
   * A token matches identifiers pattern if the token is a lowercase letter followed by a sequence of lower and upper case letter or digits
   *
   * @param token token to be checked agains identifier pattern
   * @return true if the token is an identifier, false otherwise
   */
  private static boolean matchesIdentifier(String token) {
    return token.matches("^[a-z]+[a-z|A-z|0-9]*$");
  }

  private static boolean matchesIdentifierByFiniteAutomata(String token) {
    return new IdentifierFiniteAutomata().validateSequence(token);
  }

  private static boolean matchesConstant(String token) {
    return token.matches("TRUE|FALSE") // boolean
        || token.matches("^(P#0|N#0|[N|P]#[1-9]+[0-9]*)#$") // signed integers
        || token.matches("^\"[a-z|A-Z|0-9|_]+\"$"); // non-empty strings containing letters, digits or _
  }

  private static boolean matchesConstantByFiniteAutomata(String token) {
    return new IntegerFiniteAutomata().validateSequence(token)
        || token.matches("TRUE|FALSE") // boolean
        || token.matches("^\"[a-z|A-Z|0-9|_]+\"$"); // non-empty strings containing letters, digits or _
  }
}
