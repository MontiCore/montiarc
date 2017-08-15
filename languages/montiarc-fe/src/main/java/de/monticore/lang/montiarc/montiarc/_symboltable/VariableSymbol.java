package de.monticore.lang.montiarc.montiarc._symboltable;

import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.JTypeReference;

public class VariableSymbol extends VariableSymbolTOP {


  private JTypeReference<? extends JTypeSymbol> typeReference;

  public VariableSymbol(String name) {
    super(name);
  }
  
  public JTypeReference<? extends JTypeSymbol> getTypeReference() {
    return this.typeReference;
  }

  public void setTypeReference(JTypeReference<? extends JTypeSymbol> typeReference) {
    this.typeReference = typeReference;
  }
  
}
