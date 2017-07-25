/* generated from model null*/
/* generated by template symboltable.SymbolKind*/


package de.monticore.lang.montiarc.montiarc._symboltable;

import de.monticore.symboltable.SymbolKind;

public class StateKind implements SymbolKind {

  private static final String NAME = StateKind.class.getName();

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean isKindOf(SymbolKind kind) {
    return NAME.equals(kind.getName()) || SymbolKind.super.isKindOf(kind);
  }

}
