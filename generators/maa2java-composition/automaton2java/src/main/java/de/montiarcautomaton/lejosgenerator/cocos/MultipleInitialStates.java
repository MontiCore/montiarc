package de.montiarcautomaton.lejosgenerator.cocos;

import de.monticore.automaton.ioautomaton._ast.ASTInitialStateDeclaration;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTInitialStateDeclarationCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition that forbids the usage of multiple initial states.
 *
 * @author Gerrit Leonhardt
 *
 */
public class MultipleInitialStates implements IOAutomatonASTInitialStateDeclarationCoCo {

  @Override
  public void check(ASTInitialStateDeclaration node) {
    if (node.getNames().size() > 1) {
      Log.error("0xAC110 Multiple initial states are not supported.", node.get_SourcePositionStart());
    }
  }
}
