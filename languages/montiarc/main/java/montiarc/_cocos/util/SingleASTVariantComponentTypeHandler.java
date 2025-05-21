/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.util;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._visitor.ArcBasisHandler;
import arcbasis._visitor.ArcBasisTraverser;

/**
 * Handles exactly one {@link ASTArcComponentType} and ignores any other it encounters including their subtrees.
 */
public class SingleASTVariantComponentTypeHandler implements ArcBasisHandler {

  protected ArcBasisTraverser traverser;

  protected boolean isHandlingComponent;

  @Override
  public ArcBasisTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(ArcBasisTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void handle(ASTArcComponentType node) {
    if (!isHandlingComponent) {
      isHandlingComponent = true;
      ArcBasisHandler.super.handle(node);
      isHandlingComponent = false;
    }
  }
}
