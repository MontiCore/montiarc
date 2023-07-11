/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.dse;

import java.util.List;
import java.util.Objects;

/**
 * this class is used to define the overall state of an atomic component
 */

public class StateInfo {

  /**
   * to identify each stateInfo the unique component name must be set
   */
  private String component;

  /**
   * every state has an enum state
   */
  private Enum<? extends Enum> state;

  /**
   * every state has a list of internal variables and their current values
   */
  private List<String> internalStates;

  public StateInfo(Enum<? extends Enum> state, List<String> internalStates, String component) {
    this.state = state;
    this.internalStates = internalStates;
    this.component = component;
  }

  public static StateInfo newStateInfo(Enum<? extends Enum> state, List<String> internalStates,
                                       String component) {
    return new StateInfo(state, internalStates, component);
  }

  public Enum<? extends Enum> getState() {
    return this.state;
  }

  public List<String> getInternalStates() {
    return this.internalStates;
  }

  public String getComponent() {
    return this.component;
  }

  @Override
  public String toString() {
    return "[<" + state.toString() + ">, " + String.join(" | ", internalStates) + "(" + component + ")]";
  }

  @Override
  public boolean equals(Object ob) {
    if (ob == this) {
      return true;
    }
    if (ob == null || !(ob instanceof StateInfo)) {
      return false;
    }
    StateInfo stateInfo = (StateInfo) ob;

    return stateInfo.getState().equals(getState()) && stateInfo.getInternalStates()
            .equals(getInternalStates()) && stateInfo.getComponent().equals(getComponent());
  }

  @Override
  public int hashCode() {
    return Objects.hash(component, state, internalStates);
  }
}