package symbol_table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class HashTable implements IHashTable {

  private static final int MAXIMUM_CAPACITY = 257;
  private final Node[] table;
  private int count;

  public HashTable() {
    this.table = new Node[MAXIMUM_CAPACITY];
  }

  @Override
  public Integer add(String key) {
    if (key == null) {
      return null;
    }

    if (containsKey(key)) {
      return search(key);
    }

    int bucket = hash(key);
    Node bucketTraverser = table[bucket];
    Node newNode = new Node(key, count, null);

    if (bucketTraverser != null) {
      Node currentHead = table[bucket];
      newNode.next = currentHead;
    }
    table[bucket] = newNode;

    count += 1;
    return newNode.insertionIndex;
  }

  @Override
  public Integer search(String key) {
    if (key == null) {
      return null;
    }

    int bucket = hash(key);
    Node bucketTraverser = table[bucket];

    while (bucketTraverser != null) {
      if (areKeysEquals(bucketTraverser.key, key)) {
        break;
      }

      bucketTraverser = bucketTraverser.next;
    }

    if (bucketTraverser == null) {
      return null;
    }

    return bucketTraverser.insertionIndex;
  }

  private boolean containsKey(String key) {
    return search(key) != null;
  }

  private boolean areKeysEquals(String first, String second) {
    return Objects.equals(first, second);
  }

  @Override
  public String toString() {
    List<Node> nodes = new ArrayList<>();

    for (Node node : table) {
      var head = node;

      while (head != null) {
        nodes.add(head);
        head = head.next;
      }
    }

    Collections.sort(nodes);

    return nodes.stream()
        .map(Node::toString)
        .reduce("", (accumulator, current) -> accumulator + "\n" + current);
  }

  private int hash(String key) {
    Objects.requireNonNull(key);

    int asciiCodesSum = 0;
    for (int index = 0; index < key.length(); index++) {
      int asciiCode = key.charAt(index);
      asciiCodesSum += asciiCode;
    }

    return asciiCodesSum % MAXIMUM_CAPACITY;
  }

  static private class Node implements Comparable<Node> {

    String key;
    Integer insertionIndex;
    Node next;
    public Node(String key, Integer insertionIndex, Node next) {
      this.key = key;
      this.insertionIndex = insertionIndex;
      this.next = next;
    }

    @Override
    public String toString() {
      return "Node{" +
          "key='" + key + '\'' +
          ", index=" + insertionIndex +
          '}';
    }

    @Override
    public int compareTo(Node other) {
      if (Objects.equals(this.insertionIndex, other.insertionIndex)) {
        return 0;
      } else if (this.insertionIndex < other.insertionIndex) {
        return -1;
      } else {
        return 1;
      }
    }
  }
}
