/* (c) https://github.com/MontiCore/monticore */
package dynamicmontiarc.cocos;

import de.se_rwth.commons.logging.Log;
import dynamicmontiarc._ast.ASTModeAutomaton;
import dynamicmontiarc._cocos.DynamicMontiArcASTModeAutomatonCoCo;

/**
 * Checks that each mode automaton has at least one transition.
 *
 * @author (last commit) Fuerste, Mutert
 */
public class AtLeastOneTransition implements DynamicMontiArcASTModeAutomatonCoCo {

  @Override
  public void check(ASTModeAutomaton node) {

    if(node.getModeTransitionList().size() < 1){
      Log.warn(
          "0xMA208 There are no transitions specified in the mode automaton.",
          node.get_SourcePositionStart());
    }
  }
}
