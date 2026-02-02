/* (c) https://github.com/MontiCore/monticore */
package modes._ast;

import de.monticore.scbasis._ast.ASTSCStatechartElement;
import de.monticore.scbasis._ast.ASTSCTransition;

import java.util.ArrayList;
import java.util.List;

public class ASTModeAutomaton extends ASTModeAutomatonTOP {

  /**
   * @return all transitions contained in the given mode automaton
   */
  public List<ASTSCTransition> getTransitions() {
    List<ASTSCTransition> transitions = new ArrayList<>();
    for (ASTSCStatechartElement elem : this.getSCStatechartElementList()) {
      if (elem instanceof ASTSCTransition transition) {
        transitions.add(transition);
      }
    }
    return transitions;
  }
}
