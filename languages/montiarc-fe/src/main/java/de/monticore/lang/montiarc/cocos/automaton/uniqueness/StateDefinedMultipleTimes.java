package de.monticore.lang.montiarc.cocos.automaton.uniqueness;

import java.util.HashSet;

import de.monticore.lang.montiarc.montiarc._ast.ASTAutomaton;
import de.monticore.lang.montiarc.montiarc._ast.ASTState;
import de.monticore.lang.montiarc.montiarc._ast.ASTStateDeclaration;
import de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTAutomatonCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition for checking, if a state is defined multiple times with the
 * same stereotypes.
 * 
 * @author Gerrit Leonhardt, Andreas Wortmann
 */
public class StateDefinedMultipleTimes implements MontiArcASTAutomatonCoCo {
  
  @Override
  public void check(ASTAutomaton node) {
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
