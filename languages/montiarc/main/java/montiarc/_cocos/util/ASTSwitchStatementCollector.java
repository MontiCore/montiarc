/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.util;

import de.monticore.statements.mccommonstatements._ast.ASTSwitchStatement;
import de.monticore.statements.mccommonstatements._visitor.MCCommonStatementsVisitor2;

import java.util.ArrayList;
import java.util.List;

public class ASTSwitchStatementCollector implements MCCommonStatementsVisitor2 {

  private final List<ASTSwitchStatement> expressions = new ArrayList<>();

  @Override
  public void visit(ASTSwitchStatement node) {
    expressions.add(node);
  }

  public List<ASTSwitchStatement> getExpressions() {
    return expressions;
  }

  public void clearExpressions() {
    expressions.clear();
  }
}
