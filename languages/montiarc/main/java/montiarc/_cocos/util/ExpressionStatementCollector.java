/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.util;
import java.util.ArrayList;
import java.util.List;
import de.monticore.statements.mccommonstatements._visitor.MCCommonStatementsVisitor2;
import de.monticore.statements.mccommonstatements._ast.ASTExpressionStatement;

import java.util.ArrayList;

public class ExpressionStatementCollector implements MCCommonStatementsVisitor2 {
  private final List<ASTExpressionStatement> expressions = new ArrayList<>();

  @Override
  public void visit(ASTExpressionStatement node) {
    expressions.add(node);
  }

  public List<ASTExpressionStatement> getExpressions() {
    return expressions;
  }

  public void clearExpressions(){
    expressions.clear();
  }
}
