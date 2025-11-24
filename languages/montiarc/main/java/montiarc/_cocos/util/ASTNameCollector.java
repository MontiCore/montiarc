/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.util;

import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;

import java.util.ArrayList;
import java.util.List;

public class ASTNameCollector implements ExpressionsBasisVisitor2 {
  private final List<ASTNameExpression> expressions = new ArrayList<>();

  @Override
  public void visit(ASTNameExpression node) {
    expressions.add(node);
  }

  public List<ASTNameExpression> getExpressions() {
    return expressions;
  }

  public void clearExpressions(){
    expressions.clear();
  }
}
