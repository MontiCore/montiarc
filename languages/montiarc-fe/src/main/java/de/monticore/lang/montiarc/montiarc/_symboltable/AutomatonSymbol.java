package de.monticore.lang.montiarc.montiarc._symboltable;

import java.util.ArrayList;
import java.util.List;

import de.monticore.symboltable.CommonScopeSpanningSymbol;
import de.monticore.symboltable.ImportStatement;

public class AutomatonSymbol extends CommonScopeSpanningSymbol {
  private List<String> stereoValues; 
  private List<ImportStatement> imports;
  
  public static final AutomatonKind KIND = AutomatonKind.KIND;


  public AutomatonSymbol(String name) {
    super(name, KIND);
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
