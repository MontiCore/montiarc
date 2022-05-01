/* (c) https://github.com/MontiCore/monticore */
package sim.Automaton;

import sim.comp.IComponent;

public class Configuration<T> {

  private State state;

  private String transition;

  private T value;

  private int id;

  private IComponent component;

  public Configuration(State state, String transition, T value, IComponent component) {
    this.state = state;
    this.transition = transition;
    this.value = value;
    this.component = component;
  }

  public State getState() {
    return state;
  }

  public String getTransition() {
    return transition;
  }

  //Creates a new Configuration
  public static <T> Configuration<T> of(State state, String transition, T value, IComponent component) {
    return new Configuration(state, transition, value, component);
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public T getValue() {
    return value;
  }
}
