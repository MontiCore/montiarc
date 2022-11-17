/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.check.IDerive;
import de.monticore.types.check.TypeCheck;
import de.monticore.types.check.TypeCheckResult;
import de.se_rwth.commons.logging.Log;
import montiarc.util.VariableArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.ASTArcConstraintDeclaration;
import variablearc.check.VariableArcTypeCalculator;

/**
 * Constraints must be of type boolean
 */
public class ConstraintIsBoolean implements VariableArcASTArcConstraintDeclarationCoCo {

  /**
   * Used to extract the type to which initialization expressions of fields
   * evaluate.
   */
  protected final IDerive typeCalculator;

  /**
   * Initializes the used {@link IDerive} to be a {@link
   * VariableArcTypeCalculator}.
   *
   * @see #ConstraintIsBoolean(IDerive)
   */
  public ConstraintIsBoolean() {
    this(new VariableArcTypeCalculator());
  }

  /**
   * Creates this coco with a custom {@link IDerive} used to extract the type to
   * which initialization expressions of fields evaluate.
   */
  public ConstraintIsBoolean(@NotNull IDerive typeCalculator) {
    this.typeCalculator = Preconditions.checkNotNull(typeCalculator);
  }

  @Override
  public void check(@NotNull ASTArcConstraintDeclaration astConstraint) {
    Preconditions.checkNotNull(astConstraint);

    ASTExpression expr = astConstraint.getExpression();
    TypeCheckResult typeCheckResult = typeCalculator.deriveType(expr);

    if (typeCheckResult.isPresentResult() && !TypeCheck.isBoolean(typeCheckResult.getResult())) {
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
