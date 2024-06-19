/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton;

public abstract class AutomatonBuilder<C, A extends Automaton<C>> {

  protected C context;
  protected State initial;

  public AutomatonBuilder(C context) {
    this.context = context;
  }

  public AutomatonBuilder<C, A> setContext(C context) {
    this.context = context;
    return this;
  }

  public C getContext() {
    return this.context;
  }

  public abstract AutomatonBuilder<C, A> addDefaultStates();

  public AutomatonBuilder<C, A> setInitial(State initial) {
    this.initial = initial;
    return this;
  }

  public abstract AutomatonBuilder<C, A> setDefaultInitial();

  protected State getInitial() {
    return this.initial;
  }

  public boolean isValid() {
    return this.getContext() != null
      && this.getInitial() != null;
  }

  public abstract A build();

}
