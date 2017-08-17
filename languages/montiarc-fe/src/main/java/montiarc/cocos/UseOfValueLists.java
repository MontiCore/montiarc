package montiarc.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTValueList;
import montiarc._cocos.MontiArcASTValueListCoCo;

/**
 * Context condition that forbids the usage of value lists.
 *
 * @author Gerrit Leonhardt, Andreas Wortmann
 */
public class UseOfValueLists implements MontiArcASTValueListCoCo {
  
  @Override
  public void check(ASTValueList node) {
    if (!node.getValuation().isPresent() && !node.getValuations().isEmpty()) {
      Log.error("0xAC120 Value lists are not supported.", node.get_SourcePositionStart());
    }
  }  
}
