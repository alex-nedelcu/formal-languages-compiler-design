package scanner;

import program_internal_form.IProgramInternalForm;
import symbol_table.ISymbolTable;

public class ScanResult {

  private final ISymbolTable identifiersSymbolTable;
  private final ISymbolTable constantsSymbolTable;
  private final IProgramInternalForm PIF;

  public ScanResult(ISymbolTable identifiersSymbolTable, ISymbolTable constantsSymbolTable, IProgramInternalForm PIF) {
    this.identifiersSymbolTable = identifiersSymbolTable;
    this.constantsSymbolTable = constantsSymbolTable;
    this.PIF = PIF;
  }

  public ISymbolTable getIdentifiersSymbolTable() {
    return identifiersSymbolTable;
  }

  public ISymbolTable getConstantsSymbolTable() {
    return constantsSymbolTable;
  }

  public IProgramInternalForm getPIF() {
    return PIF;
  }

  @Override
  public String toString() {
    String toReturn = ">>> SCAN RESULT <<<\n\nIDENTIFIERS SYMBOL TABLE";
    toReturn += identifiersSymbolTable.toString();
    toReturn += "\n\nCONSTANTS SYMBOL TABLE";
    toReturn += constantsSymbolTable.toString();
    toReturn += "\n\nPIF";
    toReturn += PIF.toString();

    return toReturn;
  }
}
