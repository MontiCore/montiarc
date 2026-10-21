/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;
import montiarc.util.VariableArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.ASTArcVarIf;

/**
 * If-statement's conditions should be of type boolean
 */
public class VarIfIsBoolean implements VariableArcASTArcVarIfCoCo {

  protected static final String LOG_NAME = VarIfIsBoolean.class.getSimpleName();

  public VarIfIsBoolean() { }

  @Override
  public void check(@NotNull ASTArcVarIf varif) {
    Preconditions.checkNotNull(varif);

    ASTExpression expr = varif.getCondition();
    SymTypeExpression typeOfExpr = TypeCheck3.typeOf(expr);

    if (typeOfExpr.isObscureType()) {
      Log.debug(() -> "Skip CoCo check, the type of the varif condition is obscure.", LOG_NAME);
    } else if (!SymTypeRelations.isBoolean(typeOfExpr)) {
      Log.error(VariableArcError.IF_STATEMENT_EXPRESSION_WRONG_TYPE.format(typeOfExpr.print()),
        varif.get_SourcePositionStart(), varif.get_SourcePositionEnd()
      );
    }
  }
}
