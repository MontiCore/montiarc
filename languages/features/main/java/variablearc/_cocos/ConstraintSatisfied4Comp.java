/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._cocos.ArcBasisASTComponentTypeCoCo;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.util.VariableArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._symboltable.IVariableArcComponentTypeSymbol;
import variablearc.evaluation.ExpressionSolver;

import java.util.Optional;

public class ConstraintSatisfied4Comp implements ArcBasisASTComponentTypeCoCo {

  @Override
  public void check(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());
    if (!(node.getSymbol() instanceof IVariableArcComponentTypeSymbol)) return;

    ExpressionSolver solver = new ExpressionSolver();
    Optional<Boolean> eval = solver.solve(((IVariableArcComponentTypeSymbol) node.getSymbol()).getConstraints());
    solver.close();
    if (eval.isPresent()) {
      if (!eval.get()) {
        Log.error(VariableArcError.CONSTRAINT_NOT_SATISFIED.format(),
          node.get_SourcePositionStart(), node.get_SourcePositionEnd());
      }
    } else {
      Log.debug(String.format(
        "'%s' Skipping constraint evaluation for '%s', could not calculate expression.",
        node.get_SourcePositionStart(), this.getClass().getSimpleName()), "ConstraintSatisfied4Comp");
    }
  }
}