/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import com.google.common.base.Preconditions;
import com.microsoft.z3.Context;
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

  @Override
  public void check(@NotNull ASTArcConstraintDeclaration node) {
    Preconditions.checkNotNull(node);

    if (TypeCheck3.typeOf(node.getExpression()).isObscureType()) {
      Log.debug(() -> "Skip CoCo check, the type of the constraint is obscure.", LOG_NAME);
    } else {
      Context context = new Context();
      IDeriveSMTExpr converter = VariableArcMill.fullConverter(context);
      if (converter.toBool(node.getExpression()).isEmpty()) {
        Log.warn(
          EXPRESSION_NOT_SMT_CONVERTIBLE.format(VariableArcMill.prettyPrint(node.getExpression(), false)),
          node.getExpression().get_SourcePositionStart(),
          node.getExpression().get_SourcePositionEnd());
      }
      context.close();
    }
  }
}
