/* (c) https://github.com/MontiCore/monticore */
package montiarc.trafo;

import com.google.common.base.Preconditions;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._visitor.MontiArcTraverser;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.trafo.EnforceBlocksInVarIfTrafo;

import java.util.function.UnaryOperator;

/**
 * Uses {@link EnforceBlocksInVarIfTrafo} as transformation on all {@link variablearc._ast.ASTArcVarIf} in an
 * {@link ASTMACompilationUnit}.
 */
public class MAEnforceBlocksInVarIfTrafo implements UnaryOperator<ASTMACompilationUnit> {

  private final MontiArcTraverser traverser;

  public MAEnforceBlocksInVarIfTrafo() {
    this.traverser = MontiArcMill.inheritanceTraverser();
    this.traverser.add4VariableArc(new EnforceBlocksInVarIfTrafo());
  }

  @Override
  public ASTMACompilationUnit apply(@NotNull ASTMACompilationUnit cUnit) {
    Preconditions.checkNotNull(cUnit);
    traverser.handle(cUnit);

    return cUnit;
  }
}
