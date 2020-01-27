/* (c) https://github.com/MontiCore/monticore */
package montiarc.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;

public class DeployComponentNoSignature implements MontiArcASTComponentCoCo {

  /**
   * Check that if <code>node</code> is a deploy component, then it has no ports or parameters.
   * @param node node to be checked
   */
  @Override
  public void check(ASTComponent node) {
    if (node.getStereotypeOpt().isPresent()) {
      if (node.getStereotypeOpt().get().containsStereoValue("deploy")) {
        if (!node.getPorts().isEmpty()) {
          Log.error("0xMA120 Deploy component \"" + node.getName() + "\" has ports.");
        }
        if (!node.getHead().getParameterList().isEmpty()) {
          Log.error("0xMA121 Deploy component \"" + node.getName() + "\" has parameters.");
        }
      }
    }
  }
}
