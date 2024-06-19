/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton;

public abstract class EventAutomatonBuilder<C, A extends Automaton<C>> extends AutomatonBuilder<C, A> {

  public EventAutomatonBuilder(C context) {
    super(context);
  }

  @Override
  public A build() {

    C context = this.getContext();
    State initial = this.getInitial();

    if (context == null) throw new IllegalStateException();
    if (initial == null) throw new IllegalStateException();

    return buildActual(context, initial);
  }

  protected abstract A buildActual(C context, State initial);
}
