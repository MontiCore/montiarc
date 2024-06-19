/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton;

public abstract class EventAutomaton<C> extends Automaton<C> {
  
  public EventAutomaton(C context,
                        State initial) {
    super(context, initial);
  }
  
  public abstract void tick();
}
