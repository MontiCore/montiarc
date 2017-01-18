package de.montiarcautomaton.lejosgenerator.cocos;

import de.monticore.automaton.ioautomaton._ast.ASTValueList;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTValueListCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition that forbids the usage of value lists.
 *
 * @author Gerrit Leonhardt
 *
 */
public class UseOfValueLists implements IOAutomatonASTValueListCoCo {
  
  @Override
  public void check(ASTValueList node) {
    if (!node.getValuation().isPresent() && !node.getValuations().isEmpty()) {
      Log.error("0xAC120 Value lists are not supported.", node.get_SourcePositionStart());
    }
  }  
}
