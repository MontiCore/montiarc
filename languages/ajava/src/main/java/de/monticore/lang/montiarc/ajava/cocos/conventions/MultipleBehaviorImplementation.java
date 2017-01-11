package de.monticore.lang.montiarc.ajava.cocos.conventions;

import de.monticore.lang.montiarc.montiarc._ast.ASTComponentBody;
import de.monticore.lang.montiarc.montiarc._ast.ASTElement;
import de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTComponentBodyCoCo;
import de.monticore.lang.montiarc.montiarcbehavior._ast.ASTBehaviorEmbedding;
import de.se_rwth.commons.logging.Log;

/**
 * CoCo for checking whether there are more than one behavior implementations
 * defined.
 * 
 * @author Jerome Pfeiffer
 */
public class MultipleBehaviorImplementation implements MontiArcASTComponentBodyCoCo {
  @Override
  public void check(ASTComponentBody node) {
    int counter = 0;
    for (ASTElement element : node.getElements()) {
      if (element instanceof ASTBehaviorEmbedding) {
        ++counter;
        if (counter > 1) {
          Log.error("0xAB140 Multiple behavior implementations found.",
              element.get_SourcePositionStart());
        }
      }
    }
  }
  
}
