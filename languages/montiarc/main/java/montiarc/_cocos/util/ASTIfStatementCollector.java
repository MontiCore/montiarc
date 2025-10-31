/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.util;

import de.monticore.statements.mccommonstatements._ast.ASTIfStatement;
import de.monticore.statements.mccommonstatements._visitor.MCCommonStatementsVisitor2;

import java.util.ArrayList;
import java.util.List;

public class ASTIfStatementCollector implements MCCommonStatementsVisitor2 {

  private final List<ASTIfStatement> expressions = new ArrayList<>();

  @Override
  public void visit(ASTIfStatement node) {
    expressions.add(node);
  }

  public List<ASTIfStatement> getExpressions() {
    return expressions;
  }

  public void clearExpressions() {
    expressions.clear();
  }
}
