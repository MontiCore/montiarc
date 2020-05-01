/* (c) https://github.com/MontiCore/monticore */

package montiarc.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTElement;
import montiarc._ast.ASTJavaPBehavior;
import montiarc._ast.ASTJavaPInitializer;
import montiarc._cocos.MontiArcASTComponentCoCo;

/**
 * Context condition for guaranteeing that the AJava initialization block only occurs if there is a
 * compute block
 *
 * @implements No literature reference, AJava CoCo
 */
public class InitBlockOnlyOnEmbeddedAJava implements MontiArcASTComponentCoCo {
  @Override
  public void check(ASTComponent node) {
    ASTJavaPInitializer init = null;
    boolean hasBehaviour = false;
    
    // Save the init block if there is one and determine whether there is a behaviour
    for (ASTElement element : node.getBody().getElementList()) {
      if (element instanceof ASTJavaPInitializer) {
        init = (ASTJavaPInitializer) element;
      }
      else if (element instanceof ASTJavaPBehavior) {
        hasBehaviour = true;
      }
    }
    
    // Throw an error if there is an init block present without a behaviour block.
    if (init != null && !hasBehaviour) {
      Log.error(
          "0xMA063 The component " + node.getName()
              + " contains an AJava initialization block without a behaviour block.",
          init.get_SourcePositionStart());
    }
  }
}
