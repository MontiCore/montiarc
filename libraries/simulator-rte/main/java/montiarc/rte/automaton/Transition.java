/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton;

/**
 * Represents a transition in a state machine.
 */
public class Transition {

  State source;
  State target;
  Guard guard;
  Action action;
  State current;

  protected Transition(State source,
                       State target,
                       Guard guard,
                       Action action) {
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

  protected Guard getGuard() {
    return this.guard;
  }

  protected Action getAction() {
    return this.action;
  }

  public boolean isEnabled(State current) {
    this.current = current;
    return (this.getSource().equals(current) || this.getSource().isSubstate(current)) && this.getGuard().check();
  }

  public void execute(Automaton<?> context) {
    this.action.execute();

    context.setState(this.getTarget().getInitialSubstate());
  }
}
