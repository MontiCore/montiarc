/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton;

import java.util.ArrayList;
import java.util.List;

public class StateBuilder {

  protected String name;
  protected Action<NoInput> initAction;
  protected Action<NoInput> entryAction;
  protected Action<NoInput> exitAction;
  protected Action<NoInput> doAction;
  protected List<State> substates = new ArrayList<>();
  protected List<State> initialSubstates = new ArrayList<>();

  public List<State> getInitialSubstates() {
    return initialSubstates;
  }

  public StateBuilder setInitialSubstates(List<State> initialSubstates) {
    this.initialSubstates = initialSubstates;
    return this;
  }

  public List<State> getSubstates() {
    return substates;
  }

  public StateBuilder setSubstates(List<State> substates) {
    this.substates = substates;
    return this;
  }

  public Action<NoInput> getDoAction() {
    return doAction;
  }

  public StateBuilder setDoAction(Action<NoInput> doAction) {
    this.doAction = doAction;
    return this;
  }

  public Action<NoInput> getExitAction() {
    return exitAction;
  }

  public StateBuilder setExitAction(Action<NoInput> exitAction) {
    this.exitAction = exitAction;
    return this;
  }

  public Action<NoInput> getEntryAction() {
    return entryAction;
  }

  public StateBuilder setEntryAction(Action<NoInput> entryAction) {
    this.entryAction = entryAction;
    return this;
  }

  public Action<NoInput> getInitAction() {
    return initAction;
  }

  public StateBuilder setInitAction(Action<NoInput> entryAction) {
    this.initAction = entryAction;
    return this;
  }

  public String getName() {
    return name;
  }

  public StateBuilder setName(String name) {
    this.name = name;
    return this;
  }

  public boolean isValid() {
    return this.getName() != null
      && this.getSubstates() != null
      && this.getInitialSubstates() != null;
  }

  public State build() {
    if (!isValid()) throw new IllegalStateException();

    return new State(
      this.getName(),
      this.getSubstates(),
      this.getInitialSubstates(),
      this.getInitAction(),
      this.getEntryAction(),
      this.getExitAction(),
      this.getDoAction()
    );
  }
}
