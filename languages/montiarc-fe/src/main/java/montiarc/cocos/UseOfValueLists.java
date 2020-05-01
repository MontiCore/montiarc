/* (c) https://github.com/MontiCore/monticore */
package montiarc.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTValueList;
import montiarc._cocos.MontiArcASTValueListCoCo;

/**
 * Context condition that forbids the usage of value lists.
 *
 * @implements [RRW14a] T5: Sequences of values cannot be read from or assigned to variables.
 */
public class UseOfValueLists implements MontiArcASTValueListCoCo {
  
  @Override
  public void check(ASTValueList node) {
    final boolean isSingleValue = node.isPresentValuation();
    final boolean isNonEmptyValueList = !node.getValuationList().isEmpty();
    
    if (!isSingleValue && isNonEmptyValueList) {
      Log.error("0xMA081 Value lists are not supported.", node.get_SourcePositionStart());
    }
  }
}
