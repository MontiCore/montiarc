/* (c) https://github.com/MontiCore/monticore */
package basicmodeautomata._ast;

import basicmodeautomata._symboltable.ModeSymbol;

public class ASTInitialModeDeclaration extends ASTInitialModeDeclarationTOP {

  /**
   * @return the symbol that is referenced by {@link #getMode()}
   */
  public ModeSymbol getModeSymbol(){
    return getEnclosingScope().getLocalSCStateSymbols().stream()
        .filter(s -> getMode().equals(s.getName()))
        .filter(s -> s instanceof ModeSymbol)
        .map(s -> (ModeSymbol) s)
        .findAny()
        .orElseThrow(() -> new IllegalStateException("There is no mode named "+getMode()));
  }
}