/* (c) https://github.com/MontiCore/monticore */
package comfortablearc._symboltable;

import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Deque;

public class ComfortableArcSymbolTableCreator extends ComfortableArcSymbolTableCreatorTOP {

  public ComfortableArcSymbolTableCreator(@NotNull IComfortableArcScope enclosingScope) {
    super(enclosingScope);
  }

  public ComfortableArcSymbolTableCreator(
    @NotNull Deque<? extends IComfortableArcScope> scopeStack) {
    super(scopeStack);
  }
}