/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import com.google.common.base.Preconditions;
import de.monticore.expressions.assignmentexpressions._ast.ASTDecPrefixExpression;
import de.monticore.expressions.assignmentexpressions._cocos.AssignmentExpressionsASTDecPrefixExpressionCoCo;
import org.codehaus.commons.nullanalysis.NotNull;

public class PrefixDecrementTargetsNamesOnly implements AssignmentExpressionsASTDecPrefixExpressionCoCo {

  @Override
  public void check(@NotNull ASTDecPrefixExpression decrement) {
    Preconditions.checkNotNull(decrement);
    OnlyAssignToNames.checkAssignment(decrement.getExpression(), "decrement");
  }
}