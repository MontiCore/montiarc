/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import arcbasis.util.ArcError;
import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.assignmentexpressions._cocos.AssignmentExpressionsASTAssignmentExpressionCoCo;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.se_rwth.commons.logging.Log;

public class OnlyAssignToNames implements AssignmentExpressionsASTAssignmentExpressionCoCo {

  @Override
  public void check(ASTAssignmentExpression assignment) {
    checkAssignment(assignment.getLeft(), "assign values to");
  }

  /**
   * Checks whether the given expression directly references
   * a variable or a port ({@link ASTNameExpression NameExpression})
   * or one of their attributes ({@link ASTFieldAccessExpression FieldAccess}).
   * There may be the need to add more allowed expressions (Like for example array access).
   *
   * @param expression of whatever should be overwritten
   * @param action what is tried to do here
   */
  public static void checkAssignment(ASTExpression expression, String action){
    if(!(expression instanceof ASTNameExpression) && !(expression instanceof ASTFieldAccessExpression)){
      Log.error(
        ArcError.ASSIGN_TO_NOT_NAME.format(action, expression.getClass().getSimpleName().replaceFirst("\\AAST", "")),
        expression.get_SourcePositionStart(), expression.get_SourcePositionEnd()
      );
    }
  }
}