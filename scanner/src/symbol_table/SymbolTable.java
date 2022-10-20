package symbol_table;

public class SymbolTable implements ISymbolTable {

    private final IHashTable hashTable;

    public SymbolTable() {
        this.hashTable = new HashTable();
    }

    @Override
    public Integer add(String key) {
        return (hashTable.add(key));
    }

    @Override
    public Integer search(String key) {
        return (hashTable.search(key));
    }
}
