/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import com.google.common.base.Preconditions;
import com.microsoft.z3.Context;
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

  @Override
  public void check(@NotNull ASTArcVarIf node) {
    Preconditions.checkNotNull(node);

    Context context = new Context();
    IDeriveSMTExpr converter = VariableArcMill.fullConverter(context);
    if (converter.toBool(node.getCondition()).isEmpty()) {
      Log.warn(VariableArcError.EXPRESSION_NOT_SMT_CONVERTIBLE.format(VariableArcMill.prettyPrint(node.getCondition(), false)), node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    }
    context.close();
  }
}
