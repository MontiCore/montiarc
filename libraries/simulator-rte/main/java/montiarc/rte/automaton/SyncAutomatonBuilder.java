/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton;

public abstract class SyncAutomatonBuilder<C, I, A extends Automaton<C, I>> extends AutomatonBuilder<C, I, A> {

  public SyncAutomatonBuilder(C context) {
    super(context);
  }

  @Override
  public A build() {

    C context = this.getContext();
    State initial = this.getInitial();
    String name = this.getName();

    if (context == null) throw new IllegalStateException();
    if (initial == null) throw new IllegalStateException();

    return buildActual(context, initial, name);
  }

  protected abstract A buildActual(C context, State initial, String name);
}
