/* (c) https://github.com/MontiCore/monticore */
package montiarc.conformance.util._cocos;

import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.sctransitions4code._ast.ASTTransitionAction;
import de.monticore.sctransitions4code._cocos.SCTransitions4CodeASTTransitionActionCoCo;
import de.monticore.statements.mccommonstatements._ast.ASTExpressionStatement;
import de.monticore.statements.mccommonstatements._ast.ASTMCJavaBlock;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.se_rwth.commons.logging.Log;

import static montiarc.conformance.util.AutomataUtils.print;
import static scmapping.util.MappingUtil.printPosition;

public class ActionsMustBeAssignmentsCoCo implements SCTransitions4CodeASTTransitionActionCoCo {
  private final String errorMessage =
      "SCC001 Invalid expression \"%s\" at position %s. Only Assignment Expressions are currently "
          + "supported for actions";

  @Override
  public void check(ASTTransitionAction node) {
    if (node.isPresentMCStatement()) {

      if (node.getMCStatement() instanceof ASTMCJavaBlock) {
        ASTMCJavaBlock javaBlock = (ASTMCJavaBlock) node.getMCStatement();

        for (ASTMCBlockStatement stmt : javaBlock.getMCBlockStatementList()) {
          if (stmt instanceof ASTExpressionStatement) {
            if (!(((ASTExpressionStatement) stmt).getExpression()
                instanceof ASTAssignmentExpression)) {
              Log.error(String.format(errorMessage, print(stmt), printPosition(node)));
            }
          } else {
            Log.error(String.format(errorMessage, print(stmt), printPosition(node)));
          }
        }
      } else {
        Log.error(
            String.format(errorMessage, print(node.getMCStatement()), printPosition(node)));
      }
    }
  }

  public String getErrorMessage() {
    return errorMessage;
  }
}
