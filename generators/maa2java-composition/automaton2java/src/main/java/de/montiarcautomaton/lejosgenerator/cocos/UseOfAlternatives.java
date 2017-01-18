package de.montiarcautomaton.lejosgenerator.cocos;

import de.monticore.automaton.ioautomaton._ast.ASTAlternative;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTAlternativeCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition that forbids the usage of multiple alternatives.
 *
 * @author Gerrit Leonhardt
 */
public class UseOfAlternatives implements IOAutomatonASTAlternativeCoCo {
  
  @Override
  public void check(ASTAlternative node) {
    if (node.getValueLists().size() > 1) {
      Log.error("0xAC100 Multiple alternatives are not supported.", node.get_SourcePositionStart());
    }
  }
}
