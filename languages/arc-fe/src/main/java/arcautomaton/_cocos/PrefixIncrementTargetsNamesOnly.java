/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import com.google.common.base.Preconditions;
import de.monticore.expressions.assignmentexpressions._ast.ASTIncPrefixExpression;
import de.monticore.expressions.assignmentexpressions._cocos.AssignmentExpressionsASTIncPrefixExpressionCoCo;
import org.codehaus.commons.nullanalysis.NotNull;

public class PrefixIncrementTargetsNamesOnly implements AssignmentExpressionsASTIncPrefixExpressionCoCo {

  @Override
  public void check(@NotNull ASTIncPrefixExpression increment) {
    Preconditions.checkNotNull(increment);
    OnlyAssignToNames.checkAssignment(increment.getExpression(), "increment");
  }
}