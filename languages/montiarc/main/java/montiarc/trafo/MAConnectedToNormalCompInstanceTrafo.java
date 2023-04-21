/* (c) https://github.com/MontiCore/monticore */
package montiarc.trafo;

import comfortablearc.trafo.ConnectedToNormalCompInstanceTrafo;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._visitor.MontiArcTraverser;

import java.util.function.UnaryOperator;

public class MAConnectedToNormalCompInstanceTrafo implements UnaryOperator<ASTMACompilationUnit> {

  private final MontiArcTraverser traverser;

  public MAConnectedToNormalCompInstanceTrafo() {
    this.traverser = MontiArcMill.inheritanceTraverser();
    this.traverser.add4ArcBasis(new ConnectedToNormalCompInstanceTrafo());
  }

  @Override
  public ASTMACompilationUnit apply(ASTMACompilationUnit cUnit) {
    traverser.handle(cUnit);
    return cUnit;
  }
}
