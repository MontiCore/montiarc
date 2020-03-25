/* (c) https://github.com/MontiCore/monticore */
package dynamicmontiarc.cocos;

import dynamicmontiarc.helper.DynamicMontiArcHelper;
import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;

/**
 * TODO
 *
 */
public class PortUsage implements MontiArcASTComponentCoCo {

  @Override
  public void check(ASTComponent node) {
    if (!DynamicMontiArcHelper.isDynamic(node)){
      new montiarc.cocos.PortUsage().check(node);
    }

    // We do not check that ports of the decomposed component are connected.
    // This is due to the fact that there might be static connectors that are
  }
}
