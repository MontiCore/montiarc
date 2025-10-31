/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.util;

import de.monticore.statements.mccommonstatements._ast.ASTEnhancedForControl;
import de.monticore.statements.mccommonstatements._visitor.MCCommonStatementsVisitor2;

import java.util.ArrayList;
import java.util.List;

public class ASTEnhancedForControlCollector implements MCCommonStatementsVisitor2 {

  private final List<ASTEnhancedForControl> expressions = new ArrayList<>();

  @Override
  public void visit(ASTEnhancedForControl node) {
    expressions.add(node);
  }

  public List<ASTEnhancedForControl> getExpressions() {
    return expressions;
  }

  public void clearExpressions() {
    expressions.clear();
  }

}
