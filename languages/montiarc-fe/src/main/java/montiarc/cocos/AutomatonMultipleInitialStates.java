package montiarc.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTInitialStateDeclaration;
import montiarc._cocos.MontiArcASTInitialStateDeclarationCoCo;

/**
 * Context condition that forbids the usage of multiple initial states.
 *
 * @author Gerrit Leonhardt, Andreas Wortmann
 */
public class AutomatonMultipleInitialStates implements MontiArcASTInitialStateDeclarationCoCo {

  @Override
  public void check(ASTInitialStateDeclaration node) {
    if (node.getNames().size() > 1) {
      Log.error("0xAC110 Multiple initial states are not supported.", node.get_SourcePositionStart());
    }
  }
}
