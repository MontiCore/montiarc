package montiarc.cocos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTAutomaton;
import montiarc._ast.ASTState;
import montiarc._ast.ASTStateDeclaration;
import montiarc._cocos.MontiArcASTAutomatonCoCo;

/**
 * Context condition for checking, if a state is defined multiple times with the same stereotypes.
 * 
 * @author Gerrit Leonhardt, Andreas Wortmann
 */
public class AutomatonStateDefinedMultipleTimes implements MontiArcASTAutomatonCoCo {
  
  @Override
  public void check(ASTAutomaton node) {
    List<ASTState> states = new ArrayList<>();
    Set<ASTState> duplicates = new HashSet<>();

    for (ASTStateDeclaration decl : node.getStateDeclarations()) {
      states.addAll(decl.getStates());
    }

    for (ASTState state : states) {
      int index = states.indexOf(state);
      boolean found = false;
      for(int searchIndex = index + 1; searchIndex < states.size(); searchIndex++){
        if(state.getName().equals(states.get(searchIndex).getName())){
          duplicates.add(state);
          duplicates.add(states.get(searchIndex));
        }
      }
    }

    for(ASTState state : duplicates){
      Log.error(String.format("0xMA031 State name '%s' is ambiguous.",
          state.getName()),
          state.get_SourcePositionStart());
    }
  }
}
