/* (c) https://github.com/MontiCore/monticore */
package controller;

import com.microsoft.z3.BoolExpr;
import montiarc.rte.dse.PathCondition;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * This controller implements the general structure used for a controller using transition information
 */

public class TransitionsController<In, Out> extends TerminationConditionController<In, Out> {

  // set of all visited transitions in the current computation of the component
  protected List<Pair<String, Integer>> currentCountedTransitions = new ArrayList<>();

  // set of all visited transitions in the whole dse calculation
  protected List<Pair<String, Integer>> transitionsAll = new ArrayList<>();

  // the current transition of the component, holds only for the current input element
  protected List<Pair<BoolExpr, String>> currentTransitions = new ArrayList<>();

  protected BoolExpr abortedTransitions;

  /**
   * This function saves the information of visited transitions in the current computation to the information of visited
   * transitions in the whole dse run
   */
  @Override
  protected void saveInformation() {

    if (!aborted) {
      for (Pair<String, Integer> transition : currentCountedTransitions) {
        Pair<String, Integer> duplicate = compareTransitions(transitionsAll, transition.getLeft());
        if (duplicate == null) {
          transitionsAll.add(MutablePair.of(transition.getLeft(), transition.getRight()));
        } else {
          transitionsAll.remove(duplicate);
          transitionsAll.add(MutablePair.of(duplicate.getLeft(), transition.getRight() + duplicate.getRight()));
        }
      }
    } else {
      abortConditions.add(ctx.mkNot(abortedTransitions));
    }

    currentTransitions.clear();
    currentCountedTransitions.clear();
  }

  /**
   * This function determines whether a run should be terminated based on the number of visits to a transition.
   */
  @Override
  public boolean shouldEndRun() {

    boolean currentAborted = aborted;

    for (Pair<BoolExpr, String> transitionPair : currentTransitions) {
      String transition = transitionPair.getRight();

      // get number of visits for the current transition considering all transitions visited in the dse run
      Pair<String, Integer> duplicate1 = compareTransitions(transitionsAll, transition);

      // get number of visits for the current transition considering all transitions visited in the current computation
      // of the component
      Pair<String, Integer> duplicate2 = compareTransitions(currentCountedTransitions, transition);

      // case: the transition was already visited in the current computation, as well as in the whole dse run
      if (duplicate1 != null && duplicate2 != null) {
        if (duplicate1.getRight() + duplicate2.getRight() >= maxNum) {
          if (!currentAborted) {
            abortedTransitions = transitionPair.getLeft();
          } else {
            abortedTransitions = ctx.mkAnd(abortedTransitions, transitionPair.getLeft());
          }
          aborted = true;
          currentTransitions.clear();
          return true;
        } else {
          duplicate2.setValue(duplicate2.getRight() + 1);
        }
        // case: the transition was already visited in the whole dse run
      } else if (duplicate1 != null) {
        if (duplicate1.getRight() >= maxNum) {
          if (!currentAborted) {
            abortedTransitions = transitionPair.getLeft();
          } else {
            abortedTransitions = ctx.mkAnd(abortedTransitions, transitionPair.getLeft());
          }
          aborted = true;
          currentTransitions.clear();
          return true;
        } else {
          currentCountedTransitions.add(MutablePair.of(duplicate1.getLeft(), 1));
        }
        // case: the transition was already visited in the current computation
      } else if (duplicate2 != null) {
        if (duplicate2.getRight() >= maxNum) {
          if (!currentAborted) {
            abortedTransitions = transitionPair.getLeft();
          } else {
            abortedTransitions = ctx.mkAnd(abortedTransitions, transitionPair.getLeft());
          }
          aborted = true;
          currentTransitions.clear();
          return true;
        } else {
          duplicate2.setValue(duplicate2.getRight() + 1);
        }
      } else {
        currentCountedTransitions.add(MutablePair.of(transition, 1));
      }

    }
    currentTransitions.clear();
    return false;
  }

  @Override
  public void addBranch(BoolExpr condition, String branchId) {
    if (takenBranches == null) {
      takenBranches = new PathCondition();
    }
    takenBranches.addBranch(condition, branchId);
    currentTransitions.add(ImmutablePair.of(condition, branchId));
  }

  /**
   * checks if the transition, is already part of the given pathConditions
   */
  protected Pair<String, Integer> compareTransitions(List<Pair<String, Integer>> pathConditions,
                                                     String currentTransitions) {
    for (Pair<String, Integer> path : pathConditions) {
      if (path.getLeft().equals(currentTransitions)) {
        return path;
      }
    }

    return null;
  }

  public List<Pair<BoolExpr, String>> getCurrentTransitions() {
    return currentTransitions;
  }

  public List<Pair<String, Integer>> getTransitionsAll() {
    return transitionsAll;
  }
}
