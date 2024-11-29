/* (c) https://github.com/MontiCore/monticore */
package controller;

import com.microsoft.z3.BoolExpr;
import montiarc.rte.dse.PathCondition;
import montiarc.rte.dse.StatesList;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.Set;

public class StateController<In, Out> extends TerminationConditionController<In, Out> {

  // set of all visitedStates in the current computation of the component
  protected Set<Pair<StatesList, Integer>> visitedStates = new HashSet<>();

  // set of all visited states in the whole dse calculation
  protected Set<Pair<StatesList, Integer>> visitedStatesAll = new HashSet<>();

  // the current state of the component, holds only for the current input element
  protected StatesList currentState;

  protected boolean aborted = false;
  protected PathCondition currentBranches;

  @Override
  protected void saveInformation() {
    for (Pair<StatesList, Integer> states : visitedStates) {
      Pair<StatesList, Integer> duplicate = compareStates(visitedStatesAll, states.getLeft());
      if (duplicate == null) {
        visitedStatesAll.add(MutablePair.of(states.getLeft(), states.getRight()));
      } else {
        duplicate.setValue(duplicate.getRight() + states.getRight());
      }
    }

    if (aborted) {
      abortConditions.add(ctx.mkNot(currentBranches.getBranchConditions()));
    }

    visitedStates = new HashSet<>();
    currentBranches = new PathCondition();
  }

  @Override
  public boolean shouldEndRun() {
    Pair<StatesList, Integer> duplicate1 = compareStates(visitedStatesAll, currentState);
    Pair<StatesList, Integer> duplicate2 = compareStates(visitedStates, currentState);

    if (duplicate1 != null && duplicate2 != null) {
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
      return false;
    }
  }

  @Override
  public void saveStates(StatesList info) {
    currentState = info;
  }

  @Override
  public Set<StatesList> getVisitedStates() {
    HashSet<StatesList> result = new HashSet<>();

    for (Pair<StatesList, Integer> state : visitedStatesAll) {
      result.add(state.getLeft());
    }

    return result;
  }

  public StatesList getCurrentState() {
    return currentState;
  }

  private Pair<StatesList, Integer> compareStates(Set<Pair<StatesList, Integer>> visitedStates,
                                                  StatesList currentState) {
    for (Pair<StatesList, Integer> states : visitedStates) {

      if (states.getLeft().getStates().equals(currentState.getStates())) {
        return states;
      }
    }
    return null;
  }

  public Set<StatesList> getCurrentVisitedStates() {
    Set<StatesList> result = new HashSet<>();
    for (Pair<StatesList, Integer> states : visitedStates) {
      result.add(states.getLeft());
    }
    return result;
  }

  @Override
  public void addBranch(BoolExpr condition, String branchId) {
    takenBranches.addBranch(condition, branchId);
    if (currentBranches == null) {
      currentBranches = new PathCondition();
    }
    currentBranches.addBranch(condition, branchId);
  }
}
