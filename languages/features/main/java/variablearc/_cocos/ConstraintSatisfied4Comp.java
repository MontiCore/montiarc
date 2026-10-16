/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.util.VariableArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._symboltable.IVariableArcComponentTypeSymbol;
import variablearc.evaluation.ExpressionSet;
import variablearc.evaluation.ExpressionSolver;

import java.util.Optional;

public class ConstraintSatisfied4Comp implements ArcBasisASTArcComponentTypeCoCo {

  @Override
  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());
    if (!(node.getSymbol() instanceof IVariableArcComponentTypeSymbol)) return;
    if (((IVariableArcComponentTypeSymbol) node.getSymbol()).getConstraints().isEmpty()) return;

    ExpressionSolver solver = new ExpressionSolver();
    ExpressionSet constraints = ((IVariableArcComponentTypeSymbol) node.getSymbol()).getConstraints();
    Optional<Boolean> eval = solver.solve(constraints);
    if (eval.isPresent()) {
      if (!eval.get()) {
        Log.error(VariableArcError.CONSTRAINT_NOT_SATISFIED.format(),
          node.get_SourcePositionStart(), node.get_SourcePositionEnd());
      } else if (ExpressionSolver.isUnsatisfiableForSomeUnknownValues(solver.getContext(),
        solver.getContext().mkSolver(), solver.getContext().mkAnd(solver.convert(constraints).orElseThrow()))) {
        Log.warn(VariableArcError.CONSTRAINT_NOT_SATISFIED.format(),
          node.get_SourcePositionStart(), node.get_SourcePositionEnd());
      }
    } else {
      Log.debug(() -> String.format(
        "'%s' Skipping constraint evaluation for '%s', could not calculate expression.",
        node.get_SourcePositionStart(), this.getClass().getSimpleName()), "ConstraintSatisfied4Comp");
    }
    solver.close();
  }
}
