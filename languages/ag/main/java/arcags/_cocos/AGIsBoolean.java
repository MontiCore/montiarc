/* (c) https://github.com/MontiCore/monticore */
package arcags._cocos;

import arcags._ast.ASTArcAG;
import arcags.util.AGError;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

public class AGIsBoolean implements ArcAGsASTArcAGCoCo {

  @Override
  public void check(ASTArcAG node) {
    if (node.isPresentAssume()) {
      check_condition(node.getAssume(), "assume condition");
    }
    check_condition(node.getGuarantee(), "guarantee condition");
  }

  /**
   * Check the value of a condition expression
   *
   * @param expr          The expression to be checked
   * @param conditionName The name of the expression (for logging)
   */
  void check_condition(ASTExpression expr, String conditionName) {
    SymTypeExpression conditionType = TypeCheck3.typeOf(expr);

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
        AGError.CONDITION_EXPRESSION_WRONG_TYPE.format(
          conditionName,
          conditionType.print()
        ),
        expr.get_SourcePositionStart(),
        expr.get_SourcePositionEnd()
      );
    }
  }

}
