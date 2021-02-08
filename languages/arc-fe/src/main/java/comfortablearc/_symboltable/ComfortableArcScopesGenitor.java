/* (c) https://github.com/MontiCore/monticore */
package comfortablearc._symboltable;

import arcbasis._ast.ASTComponentInstantiation;
import comfortablearc._ast.ASTFullyConnectedComponentInstantiation;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Deque;

public class ComfortableArcScopesGenitor extends ComfortableArcScopesGenitorTOP {

  public ComfortableArcScopesGenitor() {

  }

  public ComfortableArcScopesGenitor(@NotNull IComfortableArcScope enclosingScope) {
    super(enclosingScope);
  }

  public ComfortableArcScopesGenitor(
    @NotNull Deque<? extends IComfortableArcScope> scopeStack) {
    super(scopeStack);
  }

  @Override
  public void visit(@NotNull ASTFullyConnectedComponentInstantiation node) {
    visit((ASTComponentInstantiation) node);
  }

  @Override
  public void endVisit(@NotNull ASTFullyConnectedComponentInstantiation node) {
    endVisit((ASTComponentInstantiation) node);
  }
}