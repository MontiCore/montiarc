/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos.util;

import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;

import java.util.HashMap;
import java.util.Map;

public class ASTNameExpressionChangeContext implements ExpressionsBasisVisitor2 {

  private Map<String, IExpressionsBasisScope> scopeMap = new HashMap<>();

  @Override
  public void visit(ASTNameExpression node) {
    if (scopeMap.containsKey(node.getName())) {
      node.setEnclosingScope(scopeMap.get(node.getName()));
    } else {
      scopeMap.put(node.getName(), node.getEnclosingScope());
    }
  }

  public void clearScopeMap() {
    scopeMap.clear();
  }
}
