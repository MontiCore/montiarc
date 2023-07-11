/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.dse;

import java.util.List;

/**
 *  This class is used to avoid complicated type definitions in the controllers
 * when storing additional information associated with stateInfo
 */
public class StatesList {

  private List<StateInfo> states;


  public StatesList(List<StateInfo> states){
    this.states = states;
  }

  public static StatesList newStatesList(List<StateInfo> states){
    return new StatesList(states);
  }

  public List<StateInfo> getStates() {
    return this.states;
  }

  @Override
  public String toString() {
    return "[" + String.join(";", states.toString()) + "]";
  }
}
