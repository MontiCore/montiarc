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
import variablearc._ast.ASTArcIfStatement;
import variablearc.check.VariableArcTypeCalculator;

/**
 * If-statement's conditions should be of type boolean
 */
public class IfStatementIsBoolean implements VariableArcASTArcIfStatementCoCo {

  /**
   * Used to extract the type to which initialization expressions of fields
   * evaluate.
   */
  protected final IDerive typeCalculator;

  /**
   * Initializes the used {@link IDerive} to be a {@link
   * VariableArcTypeCalculator}.
   *
   * @see #IfStatementIsBoolean(IDerive)
   */
  public IfStatementIsBoolean() {
    this(new VariableArcTypeCalculator());
  }

  /**
   * Creates this coco with a custom {@link IDerive} used to extract the type to
   * which initialization expressions of fields evaluate.
   */
  public IfStatementIsBoolean(@NotNull IDerive typeCalculator) {
    this.typeCalculator = Preconditions.checkNotNull(typeCalculator);
  }

  @Override
  public void check(@NotNull ASTArcIfStatement astIfStatement) {
    Preconditions.checkNotNull(astIfStatement);

    ASTExpression expr = astIfStatement.getCondition();
    TypeCheckResult typeCheckResult = typeCalculator.deriveType(expr);

    if (typeCheckResult.isPresentResult() && !TypeCheck.isBoolean(typeCheckResult.getResult())) {
      Log.error(VariableArcError.IF_STATEMENT_EXPRESSION_WRONG_TYPE.format(typeCheckResult.getResult()
          .print()), astIfStatement.get_SourcePositionStart(),
        astIfStatement.get_SourcePositionEnd());
    }
    if (!typeCheckResult.isPresentResult()) {
      Log.debug(String.format(
        "Checking coco '%s' is skipped for the if-statement condition, as the type of the initialization " + "expression could not be calculated. "
          + "Position: '%s'.", this.getClass()
          .getSimpleName(), astIfStatement.get_SourcePositionStart()), "CoCos");
    }
  }
}
