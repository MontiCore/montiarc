/* (c) https://github.com/MontiCore/monticore */
package controller;

import montiarc.rte.dse.StateInfo;
import montiarc.rte.dse.StatesList;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Set;

/**
 * This controller looks at the overall state of the component.
 * A combination of enumStates cannot occur more than 5 times.
 * If this number is exceeded, the current run of the component is aborted.
 */
public class EnumStateController<In, Out> extends StateController<In, Out> {

  /**
   * checks if the total state, represented in a StatesList, is already part of visitedStates.
   * Because visitedStates contains also other information, it needs to be checked manually for
   * every enumState
   */
  protected Pair<StatesList, Integer> compareStates(Set<Pair<StatesList, Integer>> visitedStates, StatesList currentState) {
    boolean found = false;
    for (Pair<StatesList, Integer> states : visitedStates) {
      int i = 0;
      for (StateInfo state : states.getLeft().getStates()) {
        if (state.getState().name().equals(currentState.getStates().get(i).getState().name())) {
          found = true;
        } else {
          found = false;
          break;
        }
        i++;
      }
      if (found) {
        return states;
      }
    }
    return null;
  }
}
