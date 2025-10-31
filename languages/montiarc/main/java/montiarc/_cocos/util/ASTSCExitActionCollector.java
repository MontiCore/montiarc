/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.util;

import de.monticore.scactions._ast.ASTSCExitAction;
import de.monticore.scactions._visitor.SCActionsVisitor2;

import java.util.ArrayList;
import java.util.List;

public class ASTSCExitActionCollector implements SCActionsVisitor2 {

  private final List<ASTSCExitAction> expressions = new ArrayList<>();

  @Override
  public void visit(ASTSCExitAction node) {
    expressions.add(node);
  }

  public List<ASTSCExitAction> getExpressions() {
    return expressions;
  }

  public void clearExpressions() {
    expressions.clear();
  }
}
