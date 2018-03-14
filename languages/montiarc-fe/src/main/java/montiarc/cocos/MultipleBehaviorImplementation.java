package montiarc.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTBehaviorElement;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTElement;
import montiarc._cocos.MontiArcASTComponentCoCo;

/**
 * CoCo for checking whether there are more than one behavior implementations defined.
 *
 * @implements [Wor16] MU2: Each atomic component contains at most one behavior model. (p. 55. Lst.
 * 4.6)
 * @author Jerome Pfeiffer, Andreas Wortmann
 */
public class MultipleBehaviorImplementation implements MontiArcASTComponentCoCo {
  
  @Override
  public void check(ASTComponent node) {
    int counter = 0;
    for (ASTElement element : node.getBody().getElementList()) {
      if (element instanceof ASTBehaviorElement) {
        ++counter;
        if (counter > 1) {
          Log.error("0xMA050 Multiple behavior implementations found.",
              element.get_SourcePositionStart());
        }
      }
    }
  }
  
}
