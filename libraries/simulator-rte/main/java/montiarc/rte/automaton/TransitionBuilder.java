/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton;

/**
 * @param <T> the type of the message that triggers the transition. Use {@link NoInput} for transitions without argument
 */
public class TransitionBuilder<T> {

  State source;
  State target;
  Guard<T> guard;
  Action<T> action;

  public TransitionBuilder<T> setSource(State source) {
    this.source = source;
    return this;
  }

  protected State getSource() {
    return this.source;
  }

  public TransitionBuilder<T> setTarget(State target) {
    this.target = target;
    return this;
  }

  protected State getTarget() {
    return this.target;
  }

  public TransitionBuilder<T> setGuard(Guard<T> guard) {
    this.guard = guard;
    return this;
  }

  protected Guard<T> getGuard() {
    return this.guard;
  }

  public TransitionBuilder<T> setAction(Action<T> action) {
    this.action = action;
    return this;
  }

  protected Action<T> getAction() {
    return this.action;
  }

  public boolean isValid() {
    return this.getSource() != null
      && this.getTarget() != null
      && this.getGuard() != null
      && this.getAction() != null;
  }

  public Transition<T> build() {

    State source = this.getSource();
    State target = this.getTarget();
    Guard<T> guard = this.getGuard();
    Action<T> action = this.getAction();

    if (source == null) throw new IllegalStateException();
    if (target == null) throw new IllegalStateException();
    if (guard == null) throw new IllegalStateException();
    if (action == null) throw new IllegalStateException();

    return new Transition<>(source, target, guard, action);
  }
}
