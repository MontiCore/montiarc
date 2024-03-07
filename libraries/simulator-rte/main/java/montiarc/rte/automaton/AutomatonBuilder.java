/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton;

import java.util.ArrayList;
import java.util.List;

public abstract class AutomatonBuilder<C, A extends Automaton<C>> {

  protected C context;
  protected List<State> states;
  protected State initial;

  public AutomatonBuilder(C context, List<State> states) {
    this.context = context;
    this.states = states;
  }

  public AutomatonBuilder(C context) {
    this(context, new ArrayList<>());
  }
  
  public AutomatonBuilder() {
    this(null);
  }
  
  public AutomatonBuilder<C, A> setContext(C context) {
    this.context = context;
    return this;
  }
  
  public C getContext() {
    return this.context;
  }

  public AutomatonBuilder<C, A> addStates(List<State> states) {
    this.getStates().addAll(states);
    return this;
  }

  public AutomatonBuilder<C, A> setState(int index, State state) {
    this.getStates().set(index, state);
    return this;
  }

  public AutomatonBuilder<C, A> addState(State state) {
    this.getStates().add(state);
    return this;
  }

  public AutomatonBuilder<C, A> addState(int index, State state) {
    this.getStates().add(index, state);
    return this;
  }
  
  public abstract AutomatonBuilder<C, A> addDefaultStates();

  protected List<State> getStates() {
    return this.states;
  }

  public AutomatonBuilder<C, A> setInitial(State initial) {
    this.initial = initial.getInitialSubstate();
    return this;
  }
  
  public abstract AutomatonBuilder<C, A> setDefaultInitial();

  protected State getInitial() {
    return this.initial;
  }

  public abstract boolean isValid();

  public abstract A build();

}
