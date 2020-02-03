/* (c) https://github.com/MontiCore/monticore */
package montiarc.cocos;

import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc.visitor.TypeUniquenessVisitor;

/**
 * Check that inner components do not reuse type parameter names from the defining
 * components hierarchy.
 * @author Michael Mutert
 */
public class UniqueTypeParamsInInnerCompHierarchy implements MontiArcASTComponentCoCo {

  @Override
  public void check(ASTComponent node) {
    new TypeUniquenessVisitor().handle(node);
  }
}
