/* (c) https://github.com/MontiCore/monticore */
package montiarc.trafo;

import com.google.common.base.Preconditions;
import comfortablearc.trafo.ConnectedToNormalCompInstanceTrafo;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._visitor.MontiArcTraverser;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.function.UnaryOperator;

/**
 * Uses {@link ConnectedToNormalCompInstanceTrafo} as transformation on all {@link arcbasis._ast.ASTComponentBody} in an
 * {@link ASTMACompilationUnit}.
 */
public class MAConnectedToNormalCompInstanceTrafo implements UnaryOperator<ASTMACompilationUnit> {

  private final MontiArcTraverser traverser;

  public MAConnectedToNormalCompInstanceTrafo() {
    this.traverser = MontiArcMill.inheritanceTraverser();
    this.traverser.add4ArcBasis(new ConnectedToNormalCompInstanceTrafo());
  }

  @Override
  public ASTMACompilationUnit apply(@NotNull ASTMACompilationUnit cUnit) {
    Preconditions.checkNotNull(cUnit);

    traverser.handle(cUnit);
    return cUnit;
  }
}
