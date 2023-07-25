/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton;

import java.util.Collection;

public abstract class EventAutomaton<C> extends Automaton<C> {
  
  public EventAutomaton(C context,
                        Collection<State> states,
                        State initial) {
    super(context, states, initial);
  }
  
  public abstract void tick();
}
