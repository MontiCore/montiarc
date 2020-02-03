/* (c) https://github.com/MontiCore/monticore */
package dynamicmontiarc._symboltable;

import de.monticore.symboltable.SymbolKind;

public class ModeTransitionKind implements SymbolKind {


  private static final String NAME =
      "dynamicmontiarc._symboltable.ModeTransitionKind";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean isKindOf(SymbolKind kind) {
    return NAME.equals(kind.getName()) || SymbolKind.super.isKindOf(kind);
  }

}
