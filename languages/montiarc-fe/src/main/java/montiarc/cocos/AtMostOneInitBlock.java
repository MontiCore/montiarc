/* (c) https://github.com/MontiCore/monticore */
package montiarc.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTElement;
import montiarc._ast.ASTJavaPInitializer;
import montiarc._cocos.MontiArcASTComponentCoCo;

/**
 * Ensures that there is at most one AJava initialization block in each
 * component.
 *
 * @implements Unreferenced in literature
 */
public class AtMostOneInitBlock implements MontiArcASTComponentCoCo {
  @Override
  public void check(ASTComponent node) {
    int initCount = 0;
    
    for (ASTElement element : node.getBody().getElementList()) {
      if (element instanceof ASTJavaPInitializer) {
        if (initCount >= 1) {
          Log.error(
              "0xMA080 There is more than one AJava init block in the component" + node.getName(),
              element.get_SourcePositionStart());
        }
        else {
          initCount++;
        }
      }
    }
  }
}
