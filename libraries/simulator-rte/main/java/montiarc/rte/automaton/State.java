/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton;

import java.util.List;
import java.util.Optional;

/**
 * Represents a state in a MontiArc automaton.
 */
public class State {

  protected String name;
  protected State superState;
  protected List<State> substates;
  protected List<State> initialSubstates;
  protected Action<NoInput> entryAction;
  protected Action<NoInput> exitAction;
  protected Action<NoInput> doAction;

  /** Nullable parameters: {@code initAction, entryAction, exitAction, doAction} */
  public State(String name,
               List<State> substates,
               List<State> initialSubstates,
               Action<NoInput> entryAction,
               Action<NoInput> exitAction,
               Action<NoInput> doAction) {
    this.name = name;
    this.substates = substates;
    this.entryAction = entryAction;
    this.exitAction = exitAction;
    this.doAction = doAction;

    for (State child : substates) {
      child.setSuperState(this);
    }

    if (initialSubstates.isEmpty()) {
      this.initialSubstates = substates;
    } else {
      this.initialSubstates = initialSubstates;
    }
  }

  private void setSuperState(State superState) {
    this.superState = superState;
  }

  public String name() {
    return this.name;
  }

  /** Executes the entry action of exactly this state (and no sub states) */
  public void enter() {
    if (this.entryAction != null) this.entryAction.execute(null);
  }

  /** Executes the entry action of this state and after that of all sub states */
  public void enterWithSub() {
    enter();
    if (!initialSubstates.isEmpty())
      getDirectInitialSubstate().orElseThrow().enterWithSub();
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

  /** Executes the do action of exactly this state */
  public void doAction() {
    if (this.doAction != null) {
      this.doAction.execute(null);
    }
  }

  /**
   * Executes the do action of this specific state,
   * after having executed the do actions of all super states.
   */
  public void doActionWithSuper() {
    if (superState != null) {
      superState.doActionWithSuper();
    }
    doAction();
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

  /**
   * Get the transitive initial sub state.<br>
   * Which means:
   * <ul>
   *   <li>If this state has no sub state, this state is returned.</li>
   *   <li>If this state has sub states and they do not have children, then the initial one of them is returned.</li>
   *   <li>If this state has sub states and they do have children, then the state hierarchy will be traversed down.
   *       Only initial states will be considered when traversing down. The traversal ends with an initial sub state
   *       that has no children by itself. This state is returned.</li>
   * </ul>
   * <br>
   * Example:
   * <pre>
   * state Foo;
   * state A {
   *   initial state B {
   *     initial state C;
   *     state X;
   *     }
   *   state Y;
   *   }
   * state Z
   * }
   * </pre>
   * <ul>
   *   <li>{@code A.getInitialSubstate()} returns {@code C}</li>
   *   <li>{@code B.getInitialSubstate()} returns {@code C}</li>
   *   <li>{@code C.getInitialSubstate()} returns {@code C}</li>
   *   <li>{@code Y.getInitialSubstate()} returns {@code Y}</li>
   *   <li>{@code Foo.getInitialSubstate()} returns {@code Foo}</li>
   * </ul>
   */
  public State getInitialSubstate() {
    if (!initialSubstates.isEmpty()) {
      return getDirectInitialSubstate().orElseThrow().getInitialSubstate();
    } else {
      return this;
    }
  }

  /**
   * Returns the first initial state of this state's direct sub states.
   * (Hence, no grand children or further descendants are considered.)
   */
  public Optional<State> getDirectInitialSubstate() {
    if (initialSubstates.isEmpty()) {
      return Optional.empty();
    } else {
      return Optional.of(initialSubstates.get(0));
    }
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
