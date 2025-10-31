/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.util;

import de.monticore.scbasis._ast.ASTSCState;
import de.monticore.scbasis._visitor.SCBasisVisitor2;

import java.util.ArrayList;
import java.util.List;

public class ASTSCStateCollector implements SCBasisVisitor2 {
  private final List<ASTSCState> expressions = new ArrayList<>();

  @Override
  public void visit(ASTSCState node) {
    expressions.add(node);
  }

  public List<ASTSCState> getExpressions() {
    return expressions;
  }

  public void clearExpressions() {
    expressions.clear();
  }
}
