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

  public State(String name,
               List<State> substates,
               List<State> initialSubstates,
               Action<NoInput> initAction,
               Action<NoInput> entryAction,
               Action<NoInput> exitAction) {
    this.name = name;
    this.substates = substates;
    this.initAction = initAction;
    this.entryAction = entryAction;
    this.exitAction = exitAction;
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
    enter();
  }

  public void initWithSub() {
    init();
    if (!initialSubstates.isEmpty())
      getInitialSubstate().initWithSub();
  }

  public void enter() {
    if (this.entryAction != null) this.entryAction.execute(null);
  }

  public void enterWithSub() {
    enter();
    if (!initialSubstates.isEmpty())
      getInitialSubstate().enterWithSub();
  }

  public void exit() {
    if (this.exitAction != null) this.exitAction.execute(null);
  }

  public void exitSub(State source) {
    if (source == this || isSubstate(source)) {
      if (source != this) getSubstates().forEach(s -> s.exitSub(source));
      this.exit();
    }
  }

  public void doAction() {
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
