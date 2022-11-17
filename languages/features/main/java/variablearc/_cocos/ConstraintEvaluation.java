/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTComponentInstance;
import arcbasis._cocos.ArcBasisASTComponentInstanceCoCo;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.util.VariableArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;
import variablearc._ast.ASTArcConstraintDeclaration;
import variablearc.check.TypeExprOfVariableComponent;
import variablearc.evaluation.ExpressionSolver;

import java.util.Optional;

/**
 * We require that constraints evaluate to false of instances
 */
public class ConstraintEvaluation implements ArcBasisASTComponentInstanceCoCo {

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
      Log.debug(String.format("Checking coco '%s' is skipped for the instance, as the constraints could not be collected. " + "Position: '%s'.",
        this.getClass().getSimpleName(), astInst.get_SourcePositionStart()), "CoCos");
      return;
    }

    typeExpr.getTypeInfo().getAstNode().getBody()
      .getArcElementList().stream()
      .filter(e -> e instanceof ASTArcConstraintDeclaration)
      .map(e -> ((ASTArcConstraintDeclaration) e)).forEach(astConstraint -> {
        Optional<Boolean> eval = ExpressionSolver.solve(astConstraint.getExpression(), typeExpr, VariableArcMill.fullPrettyPrinter()::prettyprint);
        if (eval.isPresent()) {
          if (!eval.get()) {
            Log.error(VariableArcError.CONSTRAINT_NOT_SATISFIED.format(astInst.getName()), astInst.get_SourcePositionStart(), astInst.get_SourcePositionEnd());
          }
        } else {
          Log.debug(String.format("Checking coco '%s' is skipped for the constraint, as the expression could not be calculated. " + "Position: '%s'.",
            this.getClass()
              .getSimpleName(), astConstraint.get_SourcePositionStart()), "CoCos");
        }
      });
  }
}
