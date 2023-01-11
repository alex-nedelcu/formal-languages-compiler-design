package specialized_finite_automata.integer;

import java.util.HashMap;
import java.util.Map;

public class IntegerMappings {

  private static final String diezCodification = "#";
  private static final String pCodification = "P";
  private static final String nCodification = "N";
  private static final String digitCodification = "d";

  private static final Map<String, String> mappings = new HashMap<>();

  static {
    mappings.put("#", diezCodification);
    mappings.put("P", pCodification);
    mappings.put("N", nCodification);

    for (char digit = '0'; digit <= '9'; digit++) {
      mappings.put(String.valueOf(digit), digitCodification);
    }
  }

  public static String getMappingFor(String key) {
    return mappings.get(key);
  }
}
