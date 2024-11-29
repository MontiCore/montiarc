/* (c) https://github.com/MontiCore/monticore */
package controller;

import montiarc.rte.dse.StateInfo;
import montiarc.rte.dse.StatesList;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.Set;

/**
 * This controller considers the enum states individually.
 * The termination condition is calculated according to the priority of the enum states.
 * The priority order is as follows Interesting, Boring, Normal
 * The enumstate from the total state with the highest priority gives the decision the maximum
 * occurrence of the total state.
 */
public class Boring_Interesting_EnumState<In, Out> extends EnumStateController<In, Out> {

  // Set of enum states + componentName that are classified as boring from the user
  protected Set<String> boring = new HashSet<>();

  // Set of enum states + componentName that are classified as interesting from the user
  protected Set<String> interesting = new HashSet<>();

  // number of how often a boring enum state can be visited
  protected int boringCount = 1;

  // number of how often a normal enum state can be visited
  protected int normalCount = 10;

  /**
   * returns true, if the total state is counted more than the maxNumber for the enum state with
   * the highest priority
   */
  @Override
  public boolean shouldEndRun() {

    Pair<StatesList, Integer> duplicate1 = compareStates(visitedStatesAll, currentState);
    Pair<StatesList, Integer> duplicate2 = compareStates(visitedStates, currentState);

    // get maxNum according to the classification of the enum state
    int maxNum = getMaxNum(currentState);

    // if transition is classified as interesting the return value is directly false
    if (maxNum == -1) {
      if (duplicate2 != null) {
        duplicate2.setValue(duplicate2.getRight() + 1);
        return false;
      } else {
        visitedStates.add(MutablePair.of(currentState, 1));
        return false;
      }
    } else if (duplicate1 != null && duplicate2 != null) {
      if (duplicate1.getRight() + duplicate2.getRight() >= maxNum) {
        aborted = true;
        return true;
      } else {
        duplicate2.setValue(duplicate2.getRight() + 1);
        return false;
      }
    } else if (duplicate1 != null) {
      if (duplicate1.getRight() >= maxNum) {
        aborted = true;
        return true;
      } else {
        visitedStates.add(MutablePair.of(duplicate1.getLeft(), 1));
        return false;
      }
    } else if (duplicate2 != null) {
      if (duplicate2.getRight() >= maxNum) {
        aborted = true;
        return true;
      } else {
        duplicate2.setValue(duplicate2.getRight() + 1);
        return false;
      }
    } else {
      visitedStates.add(MutablePair.of(currentState, 1));
    }
    return false;
  }

  /**
   * function to calculate the maximal number of visits for a transition based on their
   * classification
   */
  private int getMaxNum(StatesList states) {
    boolean foundBoring = false;
    for (StateInfo state : states.getStates()) {
      String compareValue = state.getState().name() + state.getComponent();
      if (interesting.contains(compareValue)) {
        return -1;
      }
      if (boring.contains(compareValue)) {
        foundBoring = true;
      }
    }
    return foundBoring ? boringCount : normalCount;
  }
}
