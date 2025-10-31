/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.util;

import arccompute._ast.ASTArcCompute;
import arccompute._visitor.ArcComputeVisitor2;

import java.util.ArrayList;
import java.util.List;

public class ASTArcComputeCollector implements ArcComputeVisitor2 {

  private final List<ASTArcCompute> expressions = new ArrayList<>();

  @Override
  public void visit(ASTArcCompute node) {
    expressions.add(node);
  }

  public List<ASTArcCompute> getExpressions() {
    return expressions;
  }

  public void clearExpressions() {
    expressions.clear();
  }
}
