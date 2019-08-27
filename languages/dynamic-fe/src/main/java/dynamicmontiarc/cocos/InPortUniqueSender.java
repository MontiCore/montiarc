/* (c) https://github.com/MontiCore/monticore */
package dynamicmontiarc.cocos;

import dynamicmontiarc.helper.DynamicMontiArcHelper;
import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;

/**
 * TODO
 */
public class InPortUniqueSender implements MontiArcASTComponentCoCo {

  @Override
  public void check(ASTComponent node) {
    if(DynamicMontiArcHelper.isDynamic(node)){
      return;
    }
    new montiarc.cocos.InPortUniqueSender().check(node);
  }
}
