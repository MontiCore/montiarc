/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import com.google.common.base.Preconditions;
import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpressionsNode;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.statements.mccommonstatements._ast.ASTExpressionStatement;
import de.monticore.statements.mccommonstatements._cocos.MCCommonStatementsASTExpressionStatementCoCo;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;

public class OnlyAssignmentOrCallExpressionStatement implements MCCommonStatementsASTExpressionStatementCoCo {

  @Override
  public void check(ASTExpressionStatement node) {
    Preconditions.checkNotNull(node);

    if (!(node.getExpression() instanceof ASTAssignmentExpressionsNode || node.getExpression() instanceof ASTCallExpression)) {
      Log.error(
        ArcError.INVALID_STATEMENT.toString(),
        node.get_SourcePositionStart(), node.get_SourcePositionEnd()
      );
    }
  }
}
