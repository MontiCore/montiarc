/* (c) https://github.com/MontiCore/monticore */
package comfortablearc._symboltable;

import arcbasis._ast.ASTComponentInstantiation;
import comfortablearc._ast.ASTFullyConnectedComponentInstantiation;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Deque;

public class ComfortableArcSymbolTableCreator extends ComfortableArcSymbolTableCreatorTOP {

<<<<<<< HEAD
=======
  public ComfortableArcSymbolTableCreator() {

  }

>>>>>>> bb276d4fcc3784a5352ae1a8711ede81331f4772
  public ComfortableArcSymbolTableCreator(@NotNull IComfortableArcScope enclosingScope) {
    super(enclosingScope);
  }

  public ComfortableArcSymbolTableCreator(
    @NotNull Deque<? extends IComfortableArcScope> scopeStack) {
    super(scopeStack);
  }

  @Override
  public void visit(@NotNull ASTFullyConnectedComponentInstantiation node) {
    getRealThis().visit((ASTComponentInstantiation) node);
  }

  @Override
  public void endVisit(@NotNull ASTFullyConnectedComponentInstantiation node) {
    getRealThis().endVisit((ASTComponentInstantiation) node);
  }
}