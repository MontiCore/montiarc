package de.monticore.automaton.ioautomaton.cocos.uniqueness;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import de.monticore.automaton.ioautomaton._ast.ASTAutomatonContent;
import de.monticore.automaton.ioautomaton._ast.ASTInputDeclaration;
import de.monticore.automaton.ioautomaton._ast.ASTState;
import de.monticore.automaton.ioautomaton._ast.ASTStateDeclaration;
import de.monticore.automaton.ioautomaton._ast.ASTVariable;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTAutomatonContentCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition for checking, if a state is defined multiple times with the
 * same stereotypes.
 * 
 * @author Gerrit
 */
public class StateDefinedMultipleTimes implements IOAutomatonASTAutomatonContentCoCo {
  
  @Override
  public void check(ASTAutomatonContent node) {
    HashSet<String> names = new HashSet<>();   
    for (ASTStateDeclaration decl : node.getStateDeclarations()) {
      for (ASTState state : decl.getStates()) {
        if (names.contains(state.getName())) {
          Log.error("0xAA330 State " + state.getName() + " is defined more than once.", state.get_SourcePositionStart());
        } else {
          names.add(state.getName());
        }
      }
    }
  }
  
}
