// (c) https://github.com/MontiCore/monticore
package arcbasis.visitor;

import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor;

import java.util.ArrayList;
import java.util.List;

public class GuardExpressionVisitor implements ExpressionsBasisVisitor {

  List<ASTNameExpression> expressions = new ArrayList<>();

  public void visit(ASTNameExpression node) {
    expressions.add(node);
  }

  public List<ASTNameExpression> getExpressions() {
    return expressions;
  }
}
