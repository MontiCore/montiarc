/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTComponentInstance;
import arcbasis._cocos.ArcBasisASTComponentInstanceCoCo;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.se_rwth.commons.logging.Log;
import montiarc.util.VariableArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.ASTArcConstraintDeclaration;
import variablearc.check.TypeExprOfVariableComponent;
import variablearc.evaluation.ExpressionSolver;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ConstraintSatisfied4SubComp implements ArcBasisASTComponentInstanceCoCo {

  @Override
  public void check(@NotNull ASTComponentInstance astInst) {
    Preconditions.checkNotNull(astInst);
    Preconditions.checkArgument(astInst.isPresentSymbol());

    // CoCo does not apply to non variable components
    if (!(astInst.getSymbol().isPresentType() && astInst.getSymbol()
      .getType() instanceof TypeExprOfVariableComponent))
      return;
    TypeExprOfVariableComponent typeExpr = (TypeExprOfVariableComponent) astInst.getSymbol()
      .getType();

    if (!typeExpr.getTypeInfo().isPresentAstNode()) {
      Log.debug(String.format(
        "'%s' Skipping constraint evaluation for '%s', could not calculate expression.",
        astInst.get_SourcePositionStart(), this.getClass().getSimpleName()), "ConstraintSatisfied4SubComp");
      return;
    }

    List<ASTExpression> constraints = typeExpr.getTypeInfo().getAstNode().getBody()
      .getArcElementList().stream()
      .filter(e -> e instanceof ASTArcConstraintDeclaration)
      .map(e -> ((ASTArcConstraintDeclaration) e).getExpression())
      .collect(Collectors.toList());

    Optional<Boolean> eval = ExpressionSolver.solve(constraints, typeExpr);
    if (eval.isPresent()) {
      if (!eval.get()) {
        Log.error(VariableArcError.CONSTRAINT_NOT_SATISFIED.format(),
          astInst.get_SourcePositionStart(), astInst.get_SourcePositionEnd());
      }
    } else {
      Log.debug(String.format(
        "'%s' Skipping constraint evaluation for '%s', could not calculate expression.",
        astInst.get_SourcePositionStart(), this.getClass().getSimpleName()), "ConstraintSatisfied4SubComp");
    }
  }
}