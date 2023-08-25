/* (c) https://github.com/MontiCore/monticore */
package modes._cocos.util;


import modes._ast.ASTArcMode;
import modes._visitor.ModesHandler;
import modes._visitor.ModesTraverser;

/**
 * Ignores all {@link ASTArcMode}s and their subtree
 */
public class IgnoreASTArcModeHandler implements ModesHandler {

  protected ModesTraverser traverser;

  @Override
  public ModesTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(ModesTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void handle(ASTArcMode node) {
  }
}
