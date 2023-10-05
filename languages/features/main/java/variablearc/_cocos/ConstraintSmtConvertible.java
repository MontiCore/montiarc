/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import com.google.common.base.Preconditions;
import com.microsoft.z3.Context;
import de.se_rwth.commons.logging.Log;
import montiarc.util.VariableArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;
import variablearc._ast.ASTArcConstraintDeclaration;
import variablearc.evaluation.exp2smt.IDeriveSMTExpr;

/**
 * Warns if a constraint cannot be converted to a smt formula
 */
public class ConstraintSmtConvertible implements VariableArcASTArcConstraintDeclarationCoCo {

  @Override
  public void check(@NotNull ASTArcConstraintDeclaration node) {
    Preconditions.checkNotNull(node);

    Context context = new Context();
    IDeriveSMTExpr converter = VariableArcMill.fullConverter(context);
    if (converter.toBool(node.getExpression()).isEmpty()) {
      Log.warn(VariableArcError.EXPRESSION_NOT_SMT_CONVERTIBLE.format(VariableArcMill.prettyPrint(node.getExpression(), false)), node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    }
    context.close();
  }
}
