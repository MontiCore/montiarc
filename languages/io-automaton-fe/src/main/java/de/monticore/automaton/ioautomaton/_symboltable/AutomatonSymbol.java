package de.monticore.automaton.ioautomaton._symboltable;

import java.util.ArrayList;
import java.util.List;

import de.monticore.automaton.ioautomaton._symboltable.AutomatonSymbolTOP;
import de.monticore.symboltable.ImportStatement;

public class AutomatonSymbol extends AutomatonSymbolTOP {
  private List<String> stereoValues; 
  private List<ImportStatement> imports;

  public AutomatonSymbol(String name) {
    super(name);
    this.stereoValues = new ArrayList<>();
    this.imports = new ArrayList<>();
  }

  public List<String> getStereoValues() {
    return this.stereoValues;
  }
  
  public void addStereoValue(String stereoValue) {
    this.stereoValues.add(stereoValue);   
  }
  
  public void setImports(List<ImportStatement> imports) {
    this.imports = imports;
  }
  
  public List<ImportStatement> getImports() {
    return this.imports;
  }
  
}
