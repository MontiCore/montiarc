/* (c) https://github.com/MontiCore/monticore */
package montiarc.variability;

import montiarc.rte.automaton.SyncAutomaton;
import montiarc.rte.automaton.State;
import montiarc.rte.automaton.Transition;

import java.util.List;

public class VariabilityAut extends SyncAutomaton<VariabilityContext> {
  
  protected VariabilityAut(VariabilityContext inverterContext,
                           List<State> states,
                           List<Transition> transitions,
                           State initial) {
    super(inverterContext, states, transitions, initial);
  }
}
