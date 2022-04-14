/* (c) https://github.com/MontiCore/monticore */
package sim.generic;

import java.util.List;
import java.util.Optional;

public class Transition {

  private State beforeState;

  private State targetState;

  private String condition;

  private List<?> output;

  private IComponent component;

  public Transition(State beforeState, State targetState, String condition, IComponent component, List<?> output) {
    Optional<List<?>> o = Optional.ofNullable(output);
    this.beforeState = beforeState;
    this.targetState = targetState;
    this.condition = condition;
    this.component = component;
    this.output = o.isPresent() ? o.get() : null;
  }

  public static Transition of(State beforeState, State targetState, String condition, IComponent component,
    List<?> output) {
    return new Transition(beforeState, targetState, condition, component, output);
  }

  public State getBeforeState() {
    return beforeState;
  }

  public State getTargetState() {
    return targetState;
  }

  public String getCondition() {
    return condition;
  }

  public List<?> getOutput() {
    return output;
  }

  public IComponent getComponent() {
    return component;
  }
}
