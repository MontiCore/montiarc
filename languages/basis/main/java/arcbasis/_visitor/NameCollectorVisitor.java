/* (c) https://github.com/MontiCore/monticore */
package arcbasis._visitor;

import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class NameCollectorVisitor implements ExpressionsBasisVisitor2 {

  private final Set<String> names = new HashSet<>();

  @Override
  public void visit(ASTNameExpression node) {
    names.add(node.getName());
  }

  public Set<String> getNames() {
    return Collections.unmodifiableSet(names);
  }
}
