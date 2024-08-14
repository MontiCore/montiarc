/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton;

/**
 * Represents a transition in a state machine.
 * @param <T> the type of the message that triggers the transition. Use {@link NoInput} for transitions without argument.
 */
public class Transition<T> {
  final State source;
  final State target;
  final Guard<T> guard;
  final Action<T> action;

  protected Transition(State source,
                       State target,
                       Guard<T> guard,
                       Action<T> action) {
    this.source = source;
    this.target = target;
    this.guard = guard;
    this.action = action;
  }

  protected State getSource() {
    return this.source;
  }

  protected State getTarget() {
    return this.target;
  }

  protected Guard<T> getGuard() {
    return this.guard;
  }

  protected Action<T> getAction() {
    return this.action;
  }

  public boolean isEnabled(State current, T msg) {
    return (this.getSource().equals(current) || this.getSource().isSubstate(current))
      && this.getGuard().check(msg);
  }

  public void execute(Automaton<?, ?> context, T msg) {
    this.action.execute(msg);

    context.setState(this.getTarget().getInitialSubstate());
  }
}
