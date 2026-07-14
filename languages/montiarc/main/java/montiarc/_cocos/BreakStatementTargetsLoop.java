/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import de.monticore.statements.mccommonstatements._ast.ASTBreakStatement;
import de.monticore.statements.mccommonstatements._ast.ASTDoWhileStatement;
import de.monticore.statements.mccommonstatements._ast.ASTForStatement;
import de.monticore.statements.mccommonstatements._ast.ASTWhileStatement;
import de.monticore.statements.mccommonstatements._cocos.MCCommonStatementsASTBreakStatementCoCo;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCStatement;
import de.se_rwth.commons.logging.Log;
import montiarc.util.MontiArcError;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayDeque;
import java.util.Deque;

public class BreakStatementTargetsLoop implements MCCommonStatementsASTBreakStatementCoCo {

  protected final Deque<ASTMCStatement> breakTargets = new ArrayDeque<>();

  @Override
  public void check(@NotNull ASTBreakStatement node) {
    if (breakTargets.isEmpty()) {
      Log.error(MontiArcError.BREAK_STATEMENT_TARGETS_NO_LOOP.format(),
        node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    }
  }

  @Override
  public void visit(ASTForStatement node) {
    breakTargets.push(node);
  }

  @Override
  public void endVisit(ASTForStatement node) {
    breakTargets.pop();
  }

  @Override
  public void visit(ASTWhileStatement node) {
    breakTargets.push(node);
  }

  @Override
  public void endVisit(ASTWhileStatement node) {
    breakTargets.pop();
  }

  @Override
  public void visit(ASTDoWhileStatement node) {
    breakTargets.push(node);
  }

  @Override
  public void endVisit(ASTDoWhileStatement node) {
    breakTargets.pop();
  }

}
