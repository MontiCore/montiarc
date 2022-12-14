/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._cocos.ArcBasisASTComponentTypeCoCo;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.se_rwth.commons.logging.Log;
import montiarc.util.VariableArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.ASTArcConstraintDeclaration;
import variablearc.evaluation.ExpressionSolver;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ConstraintSatisfied4Comp implements ArcBasisASTComponentTypeCoCo {

  @Override
  public void check(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    List<ASTExpression> constraints = node.getBody()
      .getArcElementList().stream()
      .filter(e -> e instanceof ASTArcConstraintDeclaration)
      .map(e -> ((ASTArcConstraintDeclaration) e).getExpression())
      .collect(Collectors.toList());

    Optional<Boolean> eval = ExpressionSolver.solve(constraints, node.getSymbol());
    if (eval.isPresent()) {
      if (!eval.get()) {
        Log.error(VariableArcError.CONSTRAINT_NEVER_SATISFIED.format(),
          node.get_SourcePositionStart(), node.get_SourcePositionEnd());
      }
    } else {
      Log.debug(String.format(
        "'%s' Skipping constraint evaluation for '%s', could not calculate expression.",
        node.get_SourcePositionStart(), this.getClass().getSimpleName()), "ConstraintSatisfied4Comp");
    }
  }
}