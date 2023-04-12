/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis.check.IArcTypeCalculator;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.check.ITypeRelations;
import de.monticore.types.check.TypeCheckResult;
import de.se_rwth.commons.logging.Log;
import montiarc.util.VariableArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.ASTArcVarIf;

/**
 * If-statement's conditions should be of type boolean
 */
public class VarIfIsBoolean implements VariableArcASTArcVarIfCoCo {

  protected final IArcTypeCalculator tc;

  protected final ITypeRelations tr;

  public VarIfIsBoolean(@NotNull IArcTypeCalculator tc, @NotNull ITypeRelations tr) {
    this.tc = Preconditions.checkNotNull(tc);
    this.tr = Preconditions.checkNotNull(tr);
  }

  @Override
  public void check(@NotNull ASTArcVarIf varif) {
    Preconditions.checkNotNull(varif);

    ASTExpression expr = varif.getCondition();
    TypeCheckResult typeCheckResult = tc.deriveType(expr);

    if (typeCheckResult.isPresentResult() && !tr.isBoolean(typeCheckResult.getResult())) {
      Log.error(VariableArcError.IF_STATEMENT_EXPRESSION_WRONG_TYPE.format(typeCheckResult.getResult()
          .print()), varif.get_SourcePositionStart(),
        varif.get_SourcePositionEnd());
    }
    if (!typeCheckResult.isPresentResult()) {
      Log.debug(String.format(
        "Checking coco '%s' is skipped for the if-statement condition, as the type of the initialization " + "expression could not be calculated. "
          + "Position: '%s'.", this.getClass()
          .getSimpleName(), varif.get_SourcePositionStart()), "CoCos");
    }
  }
}
