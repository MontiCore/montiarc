/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton;

import java.util.List;

public abstract class EventAutomatonBuilder<C, A extends Automaton<C>> extends AutomatonBuilder<C, A> {

  public EventAutomatonBuilder(C context, List<State> states) {
    super(context, states);
  }

  public EventAutomatonBuilder(C context) {
    super(context);
  }

  public EventAutomatonBuilder() {
  }

  @Override
  public boolean isValid() {
    return this.getContext() != null
      && this.getStates() != null
      && this.getInitial() != null
      && this.getStates().contains(this.getInitial());
  }

  @Override
  public A build() {

    C context = this.getContext();
    List<State> states = this.getStates();
    State initial = this.getInitial();

    if (context == null) throw new IllegalStateException();
    if (states == null) throw new IllegalStateException();
    if (initial == null) throw new IllegalStateException();
    if (!states.contains(initial)) throw new IllegalStateException();

    return buildActual(context, states, initial);
  }

  protected abstract A buildActual(C context, List<State> states, State initial);
}
