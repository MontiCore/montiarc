/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import com.google.common.base.Preconditions;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;
import montiarc.util.VariableArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.ASTArcConstraintDeclaration;

/**
 * Constraints must be of type boolean
 */
public class ConstraintIsBoolean implements VariableArcASTArcConstraintDeclarationCoCo {

  final static String LOG_NAME = ConstraintIsBoolean.class.getSimpleName();

  @Override
  public void check(@NotNull ASTArcConstraintDeclaration node) {
    Preconditions.checkNotNull(node);

    SymTypeExpression typeOfExpr = TypeCheck3.typeOf(node.getExpression());

    if (typeOfExpr.isObscureType()) {
      Log.debug(() -> "Skip CoCo check, the type of the constraint is obscure.", LOG_NAME);
    } else if (!SymTypeRelations.isBoolean(typeOfExpr)) {
      Log.error(VariableArcError.CONSTRAINT_EXPRESSION_WRONG_TYPE.format(typeOfExpr.print()),
        node.get_SourcePositionStart(), node.get_SourcePositionEnd()
      );
    }
  }
}
