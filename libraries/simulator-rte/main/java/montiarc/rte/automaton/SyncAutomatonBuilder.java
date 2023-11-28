/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton;

import java.util.ArrayList;
import java.util.List;

public abstract class SyncAutomatonBuilder<C, A extends Automaton<C>> extends AutomatonBuilder<C, A> {

  List<Transition> trans;

  public SyncAutomatonBuilder(C context, List<State> states, List<Transition> trans) {
    super(context, states);
    this.trans = trans;
  }

  public SyncAutomatonBuilder(C context) {
    this(context, new ArrayList<>(), new ArrayList<>());
  }

  public SyncAutomatonBuilder() {
    this(null);
  }

  public abstract SyncAutomatonBuilder<C, A> addDefaultTransitions();

  public SyncAutomatonBuilder<C, A> addTransitions(List<Transition> transitions) {
    this.getTrans().addAll(transitions);
    return this;
  }

  public SyncAutomatonBuilder<C, A> setTransition(int index, Transition transition) {
    this.getTrans().set(index, transition);
    return this;
  }

  public SyncAutomatonBuilder<C, A> addTransition(Transition transition) {
    this.getTrans().add(transition);
    return this;
  }

  public SyncAutomatonBuilder<C, A> addTransition(int index, Transition transition) {
    this.getTrans().add(index, transition);
    return this;
  }

  protected List<Transition> getTrans() {
    return this.trans;
  }

  @Override
  public boolean isValid() {
    return this.getContext() != null
      && this.getStates() != null
      && this.getTrans() != null
      && this.getInitial() != null
      && this.getStates().contains(this.getInitial());
  }

  @Override
  public A build() {

    C context = this.getContext();
    List<State> states = this.getStates();
    List<Transition> trans = this.getTrans();
    State initial = this.getInitial();

    if (context == null) throw new IllegalStateException();
    if (states == null) throw new IllegalStateException();
    if (trans == null) throw new IllegalStateException();
    if (initial == null) throw new IllegalStateException();
    if (!states.contains(initial)) throw new IllegalStateException();

    return buildActual(context, states, trans, initial);
  }

  protected abstract A buildActual(C context, List<State> states, List<Transition> transitions, State initial);
}
