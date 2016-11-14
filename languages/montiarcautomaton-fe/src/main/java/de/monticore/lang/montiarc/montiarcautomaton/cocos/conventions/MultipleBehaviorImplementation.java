package de.monticore.lang.montiarc.montiarcautomaton.cocos.conventions;

import de.monticore.lang.montiarc.montiarc._ast.ASTComponentBody;
import de.monticore.lang.montiarc.montiarc._ast.ASTElement;
import de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTComponentBodyCoCo;
import de.monticore.lang.montiarc.montiarcbehavior._ast.ASTBehaviorImplementation;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition for checking, if the component only contains up to 1
 * behavior implementation.
 * 
 * @author Gerrit Leonhardt
 */
public class MultipleBehaviorImplementation implements MontiArcASTComponentBodyCoCo {
  @Override
  public void check(ASTComponentBody node) {
    int counter = 0;
    for (ASTElement element : node.getElements()) {
      if (element instanceof ASTBehaviorImplementation) {
        ++counter;
        if (counter > 1) {
          Log.error("0xAB140 Multiple behavior implementations found.", element.get_SourcePositionStart());
        }
      }
    }
  }
  
}
