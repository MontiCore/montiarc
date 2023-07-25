/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton;

public class TransitionBuilder {

  State source;
  State target;
  Guard guard;
  Action action;

  public TransitionBuilder setSource(State source) {
    this.source = source;
    return this;
  }

  protected State getSource() {
    return this.source;
  }

  public TransitionBuilder setTarget(State target) {
    this.target = target;
    return this;
  }

  protected State getTarget() {
    return this.target;
  }

  public TransitionBuilder setGuard(Guard guard) {
    this.guard = guard;
    return this;
  }

  protected Guard getGuard() {
    return this.guard;
  }

  public TransitionBuilder setAction(Action action) {
    this.action = action;
    return this;
  }

  protected Action getAction() {
    return this.action;
  }

  public boolean isValid() {
    return this.getSource() != null
      && this.getTarget() != null
      && this.getGuard() != null
      && this.getAction() != null;
  }

  public Transition build() {

    State source = this.getSource();
    State target = this.getTarget();
    Guard guard = this.getGuard();
    Action action = this.getAction();

    if (source == null) throw new IllegalStateException();
    if (target == null) throw new IllegalStateException();
    if (guard == null) throw new IllegalStateException();
    if (action == null) throw new IllegalStateException();

    return new Transition(source, target, guard, action);
  }
}
