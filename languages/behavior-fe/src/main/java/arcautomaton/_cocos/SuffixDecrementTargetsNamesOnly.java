/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import com.google.common.base.Preconditions;
import de.monticore.expressions.assignmentexpressions._ast.ASTDecSuffixExpression;
import de.monticore.expressions.assignmentexpressions._cocos.AssignmentExpressionsASTDecSuffixExpressionCoCo;
import org.codehaus.commons.nullanalysis.NotNull;

public class SuffixDecrementTargetsNamesOnly implements AssignmentExpressionsASTDecSuffixExpressionCoCo {

  @Override
  public void check(@NotNull ASTDecSuffixExpression decrement) {
    Preconditions.checkNotNull(decrement);
    OnlyAssignToNames.checkAssignment(decrement.getExpression(), "decrement");
  }
}