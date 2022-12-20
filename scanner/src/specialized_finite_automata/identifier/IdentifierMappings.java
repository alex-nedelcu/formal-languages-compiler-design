package specialized_finite_automata.identifier;

import java.util.HashMap;
import java.util.Map;

public class IdentifierMappings {

  private static final String lowerCaseLetterCodification = "0";
  private static final String upperCaseLetterCodification = "1";
  private static final String digitCodification = "2";

  private static final Map<String, String> mappings = new HashMap<>();

  static {
    for (char lowerCaseLetter = 'a'; lowerCaseLetter <= 'z'; lowerCaseLetter++) {
      mappings.put(String.valueOf(lowerCaseLetter), lowerCaseLetterCodification);
    }

    for (char upperCaseLetter = 'A'; upperCaseLetter <= 'Z'; upperCaseLetter++) {
      mappings.put(String.valueOf(upperCaseLetter), upperCaseLetterCodification);
    }

    for (char digit = '0'; digit <= '9'; digit++) {
      mappings.put(String.valueOf(digit), digitCodification);
    }
  }

  public static String getMappingFor(String key) {
    return mappings.get(key);
  }
}
