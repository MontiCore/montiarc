/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton;

public abstract class AutomatonBuilder<C, I, A extends Automaton<C, I>> {

  protected C context;
  protected State initial;
  protected String name;

  protected AutomatonBuilder(C context) {
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

  public A build() {
    if (context == null) throw new IllegalStateException();
    if (initial == null) throw new IllegalStateException();
    if (name == null) throw new IllegalStateException();

    return buildActual(context, initial, name);
  }

  protected abstract A buildActual(C context, State initial, String name);

}
