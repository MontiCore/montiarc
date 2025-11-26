/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos.util;

import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;

import java.util.LinkedHashMap;
import java.util.Map;

public class ASTFieldAccessChangeContext implements CommonExpressionsVisitor2 {

  private Map<String, IExpressionsBasisScope> scopeMap = new LinkedHashMap<>();

  @Override
  public void visit(ASTFieldAccessExpression node) {
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
