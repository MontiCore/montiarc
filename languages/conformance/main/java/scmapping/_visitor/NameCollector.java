/* (c) https://github.com/MontiCore/monticore */
package scmapping._visitor;

import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import java.util.ArrayList;
import java.util.List;

public class NameCollector implements ExpressionsBasisVisitor2 {

  List<String> names = new ArrayList<>();

  @Override
  public void visit(ASTNameExpression node) {
    names.add(node.getName());
  }

  public List<String> getNames() {
    return names;
  }
}
