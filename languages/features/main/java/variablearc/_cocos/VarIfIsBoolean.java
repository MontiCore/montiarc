/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis.check.IArcTypeCalculator;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.ISymTypeRelations;
import de.se_rwth.commons.logging.Log;
import montiarc.util.VariableArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.ASTArcVarIf;

/**
 * If-statement's conditions should be of type boolean
 */
public class VarIfIsBoolean implements VariableArcASTArcVarIfCoCo {

  protected final IArcTypeCalculator tc;

  protected final ISymTypeRelations tr;

  public VarIfIsBoolean(@NotNull IArcTypeCalculator tc, @NotNull ISymTypeRelations tr) {
    this.tc = Preconditions.checkNotNull(tc);
    this.tr = Preconditions.checkNotNull(tr);
  }

  @Override
  public void check(@NotNull ASTArcVarIf varif) {
    Preconditions.checkNotNull(varif);

    ASTExpression expr = varif.getCondition();
    SymTypeExpression typeOfExpr = this.tc.typeOf(expr);

    if (!this.tr.isBoolean(typeOfExpr)) {
      Log.error(VariableArcError.IF_STATEMENT_EXPRESSION_WRONG_TYPE.format(typeOfExpr.print()),
        varif.get_SourcePositionStart(), varif.get_SourcePositionEnd()
      );
    }
  }
}
