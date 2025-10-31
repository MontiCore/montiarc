/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.util;

import de.monticore.scdoactions._ast.ASTSCDoAction;
import de.monticore.scdoactions._visitor.SCDoActionsVisitor2;

import java.util.ArrayList;
import java.util.List;

public class ASTSCDoActionCollector implements SCDoActionsVisitor2 {

  private final List<ASTSCDoAction> expressions = new ArrayList<>();

  @Override
  public void visit(ASTSCDoAction node) {
    expressions.add(node);
  }

  public List<ASTSCDoAction> getExpressions() {
    return expressions;
  }

  public void clearExpressions() {
    expressions.clear();
  }
}
