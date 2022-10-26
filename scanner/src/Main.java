import exceptions.LexicalException;
import program_internal_form.IProgramInternalForm;
import program_internal_form.ProgramInternalForm;
import scanner.IScanner;
import scanner.Scanner;
import symbol_table.ISymbolTable;
import symbol_table.SymbolTable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

    // TODO: class diagram
    // TODO: documentation

    public static void main(String[] args) throws IOException {
        final String PROGRAM_PATH = "/Users/alexnedelcu/Desktop/Uni/FLCD/formal-languages-compiler-design/scanner/src/input/p3.txt";
        final String TOKENS_PATH = "/Users/alexnedelcu/Desktop/Uni/FLCD/formal-languages-compiler-design/scanner/src/input/token.in";
        final String OUTPUT_FILE = "/Users/alexnedelcu/Desktop/Uni/FLCD/formal-languages-compiler-design/scanner/src/output/PIF_ST.out";

        ISymbolTable identifierSymbolTable = new SymbolTable();
        ISymbolTable constantsSymbolTable = new SymbolTable();
        IProgramInternalForm PIF = new ProgramInternalForm();

        IScanner scanner = new Scanner(identifierSymbolTable, constantsSymbolTable, PIF, TOKENS_PATH);

        File outputFile = new File(OUTPUT_FILE);
        outputFile.createNewFile();
        FileWriter writer = new FileWriter(OUTPUT_FILE);

        try {
            var scanResult = scanner.scan(PROGRAM_PATH);
            writer.write(scanResult.toString());
            writer.write("\n\nScanning successfully finished");
        } catch (LexicalException lexicalException) {
            writer.write("Scanning failed!\n");
            writer.write(lexicalException.getMessage());
        } catch (IOException ioException) {
            System.out.println("Error while writing to file: " + ioException.getMessage());
        } finally {
            writer.close();
        }

    }
}
