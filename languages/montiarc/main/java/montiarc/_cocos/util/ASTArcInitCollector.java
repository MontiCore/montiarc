/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.util;

import arccompute._ast.ASTArcInit;
import arccompute._visitor.ArcComputeVisitor2;

import java.util.ArrayList;
import java.util.List;

public class ASTArcInitCollector implements ArcComputeVisitor2 {

  private final List<ASTArcInit> expressions = new ArrayList<>();

  @Override
  public void visit(ASTArcInit node) {
    expressions.add(node);
  }

  public List<ASTArcInit> getExpressions() {
    return expressions;
  }

  public void clearExpressions() {
    expressions.clear();
  }
}
