/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis.check.IArcTypeCalculator;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.check.IDerive;
import de.monticore.types.check.ITypeRelations;
import de.monticore.types.check.TypeCheckResult;
import de.se_rwth.commons.logging.Log;
import montiarc.util.VariableArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.ASTArcConstraintDeclaration;

/**
 * Constraints must be of type boolean
 */
public class ConstraintIsBoolean implements VariableArcASTArcConstraintDeclarationCoCo {

  protected final IArcTypeCalculator tc;

  protected final ITypeRelations tr;

  /**
   * Creates this coco with a custom {@link IDerive} used to extract the type to
   * which initialization expressions of fields evaluate.
   */
  public ConstraintIsBoolean(@NotNull IArcTypeCalculator tc, @NotNull ITypeRelations tr) {
    this.tc = Preconditions.checkNotNull(tc);
    this.tr = Preconditions.checkNotNull(tr);
  }

  @Override
  public void check(@NotNull ASTArcConstraintDeclaration astConstraint) {
    Preconditions.checkNotNull(astConstraint);

    ASTExpression expr = astConstraint.getExpression();
    TypeCheckResult typeCheckResult = tc.deriveType(expr);

    if (typeCheckResult.isPresentResult() && !tr.isBoolean(typeCheckResult.getResult())) {
      Log.error(VariableArcError.CONSTRAINT_EXPRESSION_WRONG_TYPE.format(typeCheckResult.getResult()
          .print()), astConstraint.get_SourcePositionStart(),
        astConstraint.get_SourcePositionEnd());
    }
    if (!typeCheckResult.isPresentResult()) {
      Log.debug(String.format(
        "Checking coco '%s' is skipped for the constraint, as the type of the initialization " + "expression could not be calculated. "
          + "Position: '%s'.", this.getClass()
          .getSimpleName(), astConstraint.get_SourcePositionStart()), "CoCos");
    }
  }
}
