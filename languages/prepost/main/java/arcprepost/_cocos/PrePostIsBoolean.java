/* (c) https://github.com/MontiCore/monticore */
package arcprepost._cocos;

import arcprepost._ast.ASTArcPrePost;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;
import montiarc.util.PrePostError;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Check that both the pre and post condition evaluate to boolean
 */
public class PrePostIsBoolean implements ArcPrePostASTArcPrePostCoCo {

  @Override
  public void check(@NotNull ASTArcPrePost node) {
    Preconditions.checkNotNull(node);
    if (node.isPresentPre()) {
      check_condition(node.getPre(), "pre condition");
    }
    check_condition(node.getPost(), "post condition");
  }

  /**
   * Check the value of a condition expression
   *
   * @param expr          The expression to be checked
   * @param conditionName The name of the expression (for logging)
   */
  void check_condition(ASTExpression expr, String conditionName) {
    var conditionType = TypeCheck3.typeOf(expr);

    if (conditionType.isObscureType()) {
      Log.debug(() -> String.format(
          "Skip the %s type check. The type is obscure. An error should have already been logged.", conditionName),
        expr.get_SourcePositionStart(),
        expr.get_SourcePositionEnd(),
        this.getClass().getSimpleName()
      );
      return;
    }

    if (!SymTypeRelations.isBoolean(conditionType)) {
      Log.error(
        PrePostError.CONDITION_EXPRESSION_WRONG_TYPE.format(
          conditionName,
          conditionType.print()
        ),
        expr.get_SourcePositionStart(),
        expr.get_SourcePositionEnd()
      );
    }
  }
}
