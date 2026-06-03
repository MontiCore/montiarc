/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import com.google.common.base.Preconditions;
import com.microsoft.z3.Context;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.se_rwth.commons.logging.Log;
import montiarc.util.VariableArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;
import variablearc._ast.ASTArcVarIf;
import variablearc.evaluation.exp2smt.IDeriveSMTExpr;

/**
 * Warns if a varif cannot be converted to a smt formula
 */
public class VarIfSmtConvertible implements VariableArcASTArcVarIfCoCo {

  final static String DEFAULT_REASON = "expressions of this kind are not supported";

  @Override
  public void check(@NotNull ASTArcVarIf node) {
    Preconditions.checkNotNull(node);

    Context context = new Context();
    IDeriveSMTExpr converter = VariableArcMill.fullConverter(context);
    if (converter.toBool(node.getCondition()).isEmpty()) {
      ASTExpression cause = converter.getResult().getFailureCause().orElse(node.getCondition());
      String reason = converter.getResult().getFailureReason().orElse(DEFAULT_REASON);
      Log.warn(VariableArcError.EXPRESSION_NOT_SMT_CONVERTIBLE.format(
          VariableArcMill.prettyPrint(cause, false), reason),
        cause.get_SourcePositionStart(), cause.get_SourcePositionEnd());
    }
    context.close();
  }
}
