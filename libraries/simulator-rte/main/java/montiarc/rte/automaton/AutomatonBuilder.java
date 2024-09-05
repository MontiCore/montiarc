/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton;

public abstract class AutomatonBuilder<C, I, A extends Automaton<C, I>> {

  protected C context;
  protected State initial;
  protected String name;

  public AutomatonBuilder(C context) {
    this.context = context;
  }

  public AutomatonBuilder<C, I, A> setContext(C context) {
    this.context = context;
    return this;
  }

  public C getContext() {
    return this.context;
  }

  public abstract AutomatonBuilder<C, I, A> addDefaultStates();

  public AutomatonBuilder<C, I, A> setInitial(State initial) {
    this.initial = initial;
    return this;
  }

  public abstract AutomatonBuilder<C, I, A> setDefaultInitial();

  public AutomatonBuilder<C, I, A> setName(String name) {
    this.name = name;
    return this;
  }

  public String getName() {
    return this.name;
  }

  protected State getInitial() {
    return this.initial;
  }

  public boolean isValid() {
    return this.getContext() != null
      && this.getInitial() != null
      && this.getName() != null;
  }

  public abstract A build();

}
