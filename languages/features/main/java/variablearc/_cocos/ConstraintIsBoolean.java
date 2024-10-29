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
import variablearc._ast.ASTArcConstraintDeclaration;

/**
 * Constraints must be of type boolean
 */
public class ConstraintIsBoolean implements VariableArcASTArcConstraintDeclarationCoCo {

  public ConstraintIsBoolean() { }

  @Override
  public void check(@NotNull ASTArcConstraintDeclaration astConstraint) {
    Preconditions.checkNotNull(astConstraint);

    ASTExpression expr = astConstraint.getExpression();
    SymTypeExpression typeOfExpr = TypeCheck3.typeOf(expr);

    if (!SymTypeRelations.isBoolean(typeOfExpr)) {
      Log.error(VariableArcError.CONSTRAINT_EXPRESSION_WRONG_TYPE.format(typeOfExpr.print()),
        astConstraint.get_SourcePositionStart(), astConstraint.get_SourcePositionEnd()
      );
    }
  }
}
