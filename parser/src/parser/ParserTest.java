package parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import grammar.Grammar;
import grammar.IGrammar;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ParserTest {

  // nonEmptyGrammar
  // S -> ABA
  // A -> CD | a
  // B -> EF | b
  // C -> c | EPSILON
  // D -> d
  // E -> eE | EPSILON
  // F -> fF | EPSILON

  // emptyGrammar
  // S -> A

  private static final String EPSILON = "EPSILON";
  private IParser parser;
  private IGrammar nonEmptyGrammar;
  private IGrammar emptyGrammar;

  @BeforeEach
  void setUp() {
    nonEmptyGrammar = new Grammar("/Users/alexandru.nedelcu.ext/Desktop/Other/uni/FLCD/formal-languages-compiler-design/parser/src/data/g1.txt");
    nonEmptyGrammar.process();

    emptyGrammar = new Grammar("/Users/alexandru.nedelcu.ext/Desktop/Other/Uni/FLCD/formal-languages-compiler-design/parser/src/data/g3.txt");
    emptyGrammar.process();
  }

  @Test
  void testFirstForNonEmptyGrammar() {
    parser = new Parser(nonEmptyGrammar);
    Map<String, List<String>> first = parser.getFirst();

    assertTrue(first.get("S").contains("a"));
    assertTrue(first.get("S").contains("c"));

    assertTrue(first.get("A").contains("a"));
    assertTrue(first.get("A").contains("c"));

    assertTrue(first.get("D").contains("d"));

    assertTrue(first.get("F").contains("f"));
    assertTrue(first.get("F").contains(EPSILON));
  }

  @Test
  void testFollowForNonEmptyGrammar() {
    parser = new Parser(nonEmptyGrammar);
    Map<String, List<String>> follow = parser.getFollow();

    assertTrue(follow.get("S").contains(EPSILON));
    assertEquals(1, follow.get("S").size());

    assertTrue(follow.get("A").contains("a"));
    assertTrue(follow.get("A").contains("b"));
    assertTrue(follow.get("A").contains("c"));

    assertTrue(follow.get("C").contains("d"));

    assertTrue(follow.get("F").contains("a"));
    assertTrue(follow.get("F").contains("c"));
  }

  @Test
  void testFollowForEmptyGrammar() {
    parser = new Parser(emptyGrammar);
    Map<String, List<String>> follow = parser.getFollow();

    assertTrue(follow.get("S").contains(EPSILON));
    assertTrue(follow.get("A").contains(EPSILON));
  }
}