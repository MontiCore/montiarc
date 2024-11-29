/* (c) https://github.com/MontiCore/monticore */
package controller;

import com.microsoft.z3.BoolExpr;
import montiarc.rte.dse.PathCondition;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class TransitionsController<In, Out> extends TerminationConditionController<In, Out> {

  protected List<Pair<String, Integer>> currentCountedTransitions = new ArrayList<>();

  protected List<Pair<String, Integer>> transitionsAll = new ArrayList<>();

  protected List<Pair<BoolExpr, String>> currentTransitions = new ArrayList<>();

  protected BoolExpr abortedTransitions;

  @Override
  protected void saveInformation() {

    if (!aborted) {
      for (Pair<String, Integer> transition : currentCountedTransitions) {
        Pair<String, Integer> duplicate = compareTransitions(transitionsAll, transition.getLeft());
        if (duplicate == null) {
          transitionsAll.add(MutablePair.of(transition.getLeft(), transition.getRight()));
        } else {
          duplicate.setValue(duplicate.getRight() + transition.getRight());
        }
      }
    } else {
      abortConditions.add(ctx.mkNot(abortedTransitions));
    }

    currentTransitions.clear();
    currentCountedTransitions.clear();
  }

  /**
   * returns true, if a transition was already taken
   *
   */
  @Override
  public boolean shouldEndRun() {

    boolean currentAborted = aborted;
    for (Pair<BoolExpr, String> transitionPair : currentTransitions) {
      String transition = transitionPair.getRight();
      Pair<String, Integer> duplicate1 = compareTransitions(transitionsAll, transition);
      Pair<String, Integer> duplicate2 = compareTransitions(currentCountedTransitions, transition);

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
