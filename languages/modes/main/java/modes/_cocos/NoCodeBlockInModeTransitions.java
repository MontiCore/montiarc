/* (c) https://github.com/MontiCore/monticore */
package modes._cocos;

import de.monticore.scbasis._ast.ASTSCTransition;
import de.monticore.sctransitions4code._ast.ASTTransitionBody;
import de.se_rwth.commons.logging.Log;
import modes._ast.ASTModeAutomaton;
import montiarc.util.ModesError;

public class NoCodeBlockInModeTransitions implements ModesASTModeAutomatonCoCo {

  @Override
  public void check(ASTModeAutomaton mode) {
    for (ASTSCTransition transition : mode.getTransitions()) {
      if (transition.getSCTBody() instanceof ASTTransitionBody body
        && body.isPresentTransitionAction()) {

        Log.error(ModesError.MODE_AUTOMATON_TRANSITION_CONTAINS_ACTION.format(),
          body.getTransitionAction().get_SourcePositionStart(),
          body.getTransitionAction().get_SourcePositionEnd()
        );
      }
    }
  }
}
