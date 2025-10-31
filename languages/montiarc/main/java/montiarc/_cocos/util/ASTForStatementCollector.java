/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.util;

import de.monticore.statements.mccommonstatements._ast.ASTForStatement;
import de.monticore.statements.mccommonstatements._visitor.MCCommonStatementsVisitor2;

import java.util.ArrayList;
import java.util.List;

public class ASTForStatementCollector implements MCCommonStatementsVisitor2 {

  private final List<ASTForStatement> expressions = new ArrayList<>();

  @Override
  public void visit(ASTForStatement node) {
    expressions.add(node);
  }

  public List<ASTForStatement> getExpressions() {
    return expressions;
  }

  public void clearExpressions() {
    expressions.clear();
  }
}
