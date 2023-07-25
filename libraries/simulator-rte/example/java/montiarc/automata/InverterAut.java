/* (c) https://github.com/MontiCore/monticore */
package montiarc.automata;

import montiarc.rte.automaton.SyncAutomaton;
import montiarc.rte.automaton.State;
import montiarc.rte.automaton.Transition;

import java.util.List;

public class InverterAut extends SyncAutomaton<InverterContext> {

  protected InverterAut(InverterContext inverterContext,
                        List<State> states,
                        List<Transition> transitions,
                        State initial) {
    super(inverterContext, states, transitions, initial);
  }
}
