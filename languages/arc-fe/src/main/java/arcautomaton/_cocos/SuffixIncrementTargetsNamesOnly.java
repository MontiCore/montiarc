/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import com.google.common.base.Preconditions;
import de.monticore.expressions.assignmentexpressions._ast.ASTIncSuffixExpression;
import de.monticore.expressions.assignmentexpressions._cocos.AssignmentExpressionsASTIncSuffixExpressionCoCo;
import org.codehaus.commons.nullanalysis.NotNull;

public class SuffixIncrementTargetsNamesOnly implements AssignmentExpressionsASTIncSuffixExpressionCoCo {

  @Override
  public void check(@NotNull ASTIncSuffixExpression increment) {
    Preconditions.checkNotNull(increment);
    OnlyAssignToNames.checkAssignment(increment.getExpression(), "increment");
  }
}