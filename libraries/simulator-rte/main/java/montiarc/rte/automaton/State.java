/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton;

import java.util.List;

/**
 * Represents a state in a MontiArc automaton.
 */
public class State {

  protected String name;
  protected List<State> substates;
  protected List<State> initialSubstates;
  protected Action<NoInput> initAction;
  protected Action<NoInput> entryAction;
  protected Action<NoInput> exitAction;
  protected Action<NoInput> doAction;

  public State(String name,
               List<State> substates,
               List<State> initialSubstates,
               Action<NoInput> initAction,
               Action<NoInput> entryAction,
               Action<NoInput> exitAction,
               Action<NoInput> doAction) {
    this.name = name;
    this.substates = substates;
    this.initAction = initAction;
    this.entryAction = entryAction;
    this.exitAction = exitAction;
    this.doAction = doAction;

    if (initialSubstates.isEmpty()) {
      this.initialSubstates = substates;
    } else {
      this.initialSubstates = initialSubstates;
    }
  }

  public String name() {
    return this.name;
  }

  public void init() {
    if (initAction != null) this.initAction.execute(null);
  }

  /** Executes the entry action of exactly this state (and no sub states) */
  public void enter() {
    if (this.entryAction != null) this.entryAction.execute(null);
  }

  /** Executes the entry action of this state and after that of all sub states */
  public void enterWithSub() {
    enter();
    if (!initialSubstates.isEmpty())
      getInitialSubstate().enterWithSub();
  }

  /** Executes the exit action of exactly this state (and no sub state) */
  public void exit() {
    if (this.exitAction != null) this.exitAction.execute(null);
  }

  /**
   * Given that {@code source} is in this state or one of its sub states, all exit action from {@code source} up to this
   * state are executed.
   */
  public void exitSub(State source) {
    if (isSubstate(source)) {
      getSubstates().forEach(s -> s.exitSub(source));
      this.exit();
    } else if (source == this) {
      this.exit();
    }
  }

  public void doAction() {
    if (this.doAction != null) {
      this.doAction.execute(null);
    }
  }

  @Override
  public boolean equals(Object obj) {
    return this == obj;
  }

  public List<State> getSubstates() {
    return substates;
  }

  public List<State> getInitialSubstates() {
    return initialSubstates;
  }

  public State getInitialSubstate() {
    if (initialSubstates.isEmpty())
      return this;
    return initialSubstates.get(0).getInitialSubstate();
  }

  public boolean isSubstate(State current) {
    if (this.getSubstates().contains(current)) {
      return true;
    } else {
      for (State s : substates) {
        if (s.isSubstate(current))
          return true;
      }
    }
    return false;
  }
}
