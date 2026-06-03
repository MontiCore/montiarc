/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import com.google.common.base.Preconditions;
import com.microsoft.z3.Context;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;
import variablearc._ast.ASTArcConstraintDeclaration;
import variablearc.evaluation.exp2smt.IDeriveSMTExpr;

import static montiarc.util.VariableArcError.EXPRESSION_NOT_SMT_CONVERTIBLE;

/**
 * Warns if a constraint cannot be converted to a smt formula
 */
public class ConstraintSmtConvertible implements VariableArcASTArcConstraintDeclarationCoCo {

  final static String LOG_NAME = ConstraintSmtConvertible.class.getSimpleName();

  final static String DEFAULT_REASON = "expressions of this kind are not supported";

  @Override
  public void check(@NotNull ASTArcConstraintDeclaration node) {
    Preconditions.checkNotNull(node);

    if (TypeCheck3.typeOf(node.getExpression()).isObscureType()) {
      Log.debug(() -> "Skip CoCo check, the type of the constraint is obscure.", LOG_NAME);
    } else {
      Context context = new Context();
      IDeriveSMTExpr converter = VariableArcMill.fullConverter(context);
      if (converter.toBool(node.getExpression()).isEmpty()) {
        ASTExpression cause = converter.getResult().getFailureCause().orElse(node.getExpression());
        String reason = converter.getResult().getFailureReason().orElse(DEFAULT_REASON);
        Log.warn(
          EXPRESSION_NOT_SMT_CONVERTIBLE.format(VariableArcMill.prettyPrint(cause, false), reason),
          cause.get_SourcePositionStart(),
          cause.get_SourcePositionEnd());
      }
      context.close();
    }
  }
}
