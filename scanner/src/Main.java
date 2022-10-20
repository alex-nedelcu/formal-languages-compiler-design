import symbol_table.ISymbolTable;
import symbol_table.SymbolTable;

public class Main {

    /* Task 1b */
    
    public static void main(String[] args) {
        ISymbolTable symbolTable = new SymbolTable();

        System.out.println(symbolTable.add("abcd"));
        System.out.println(symbolTable.add("bacd"));
        System.out.println(symbolTable.add("dabc"));
        System.out.println(symbolTable.add("dbca"));
        System.out.println("-----");
        System.out.println(symbolTable.search("abcd"));
        System.out.println(symbolTable.search("bacd"));
        System.out.println(symbolTable.search("dabc"));
        System.out.println(symbolTable.search("dbca"));
        System.out.println("-----");
        System.out.println(symbolTable.add("123"));
        System.out.println(symbolTable.search("213"));
        System.out.println("-----");
        System.out.println(symbolTable.add("x"));
        System.out.println(symbolTable.add("x"));
        System.out.println(symbolTable.add("x"));
        System.out.println(symbolTable.search("x"));
        System.out.println("-----");
        System.out.println(symbolTable.add("y"));
        System.out.println(symbolTable.search("dabc"));
    }
}
