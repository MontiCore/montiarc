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
    return this.getSource().equals(current) && this.getGuard().check();
  }

  public void execute() {
    this.getSource().exit();

    this.getAction().execute();

    this.getTarget().enter();
  }
}
