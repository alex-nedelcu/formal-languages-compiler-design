package symbol_table;

import lombok.Builder;

import java.util.Objects;

public class HashTable implements IHashTable {

    private static final int MAXIMUM_CAPACITY = 257;

    @Builder
    static private class Node {
        String key;
        Integer insertionIndex;
        Node next;
    }

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
        Node newNode = Node.builder()
                .key(key)
                .insertionIndex(count)
                .next(null)
                .build();

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

    private int hash(String key) {
        Objects.requireNonNull(key);

        int asciiCodesSum = 0;
        for (int index = 0; index < key.length(); index++) {
            int asciiCode = key.charAt(index);
            asciiCodesSum += asciiCode;
        }

        return asciiCodesSum % MAXIMUM_CAPACITY;
    }
}
