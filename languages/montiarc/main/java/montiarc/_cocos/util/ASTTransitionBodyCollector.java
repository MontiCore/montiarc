/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.util;

import de.monticore.sctransitions4code._ast.ASTTransitionBody;
import de.monticore.sctransitions4code._visitor.SCTransitions4CodeVisitor2;

import java.util.ArrayList;
import java.util.List;

public class ASTTransitionBodyCollector implements SCTransitions4CodeVisitor2 {

  private final List<ASTTransitionBody> expressions = new ArrayList<>();

  @Override
  public void visit(ASTTransitionBody node) {
    expressions.add(node);
  }

  public List<ASTTransitionBody> getExpressions() {
    return expressions;
  }

  public void clearExpressions() {
    expressions.clear();
  }
}
