/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos.util;

import arcbasis._ast.ASTComponentType;
import arcbasis._visitor.ArcBasisHandler;
import arcbasis._visitor.ArcBasisTraverser;

/**
 * Handles exactly one {@link ASTComponentType} and ignores any other it encounters including their subtrees.
 */
public class SingleASTVariantComponentTypeHandler implements ArcBasisHandler {

  protected ArcBasisTraverser traverser;

  protected ASTComponentType startingComponent;

  @Override
  public ArcBasisTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(ArcBasisTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void handle(ASTComponentType node) {
    if (startingComponent == null || node == startingComponent) {
      startingComponent = node;
      ArcBasisHandler.super.handle(node);
    }
  }
}
