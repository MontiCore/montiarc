/* (c) https://github.com/MontiCore/monticore */
package dynamicmontiarc.cocos;

import dynamicmontiarc.visitor.TypeUniquenessVisitor;
import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;

public class UniqueTypeParamsInInnerCompHierarchy
    implements MontiArcASTComponentCoCo {

  @Override
  public void check(ASTComponent node) {
    new TypeUniquenessVisitor().handle(node);
  }
}
