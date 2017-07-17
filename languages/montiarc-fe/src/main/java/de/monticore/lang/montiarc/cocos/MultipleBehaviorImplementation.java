package de.monticore.lang.montiarc.cocos;

import de.monticore.lang.montiarc.montiarc._ast.ASTBehaviorElement;
import de.monticore.lang.montiarc.montiarc._ast.ASTComponent;
import de.monticore.lang.montiarc.montiarc._ast.ASTElement;
import de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTComponentCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * CoCo for checking whether there are more than one behavior implementations
 * defined.
 * 
 * @author Jerome Pfeiffer, Andreas Wortmann
 */
public class MultipleBehaviorImplementation implements MontiArcASTComponentCoCo {
	
  @Override
  public void check(ASTComponent node) {
    int counter = 0;
    for (ASTElement element : node.getBody().getElements()) {
      if (element instanceof ASTBehaviorElement) {
        ++counter;
        if (counter > 1) {
          Log.error("0xAB140 Multiple behavior implementations found.",
              element.get_SourcePositionStart());
        }
      }
    }
  }
  
}
