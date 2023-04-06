/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos.util;

import variablearc._ast.ASTArcIfStatement;
import variablearc._visitor.VariableArcHandler;
import variablearc._visitor.VariableArcTraverser;

/**
 * Ignores all {@link ASTArcIfStatement}s and their subtree
 */
public class IgnoreASTArcIfStatementHandler implements VariableArcHandler {

  protected VariableArcTraverser traverser;

  @Override
  public VariableArcTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(VariableArcTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void handle(ASTArcIfStatement node) {
  }
}
