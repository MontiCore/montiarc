/* (c) https://github.com/MontiCore/monticore */
package controller;

import com.microsoft.z3.BoolExpr;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * This controller considers the transitions individually.
 * The termination condition is calculated according to the priority of the transitions.
 * The priority order is as follows Interesting, Boring, Normal
 * The transition from the total state, of transitions, with the highest priority gives the
 * decision the maximum occurrence of the total state.
 */
public class Boring_Interesting_Transitions<In, Out> extends TransitionsController<In, Out> {

  // Set of transition names that are classified as boring from the user
  protected Set<String> boring = new LinkedHashSet<>();

  // Set of transition names that are classified as interesting from the user
  protected Set<String> interesting = new LinkedHashSet<>();

  // number of how often a boring transition can be taken
  protected int boringCount = 1;

  // number of how often a normal transition can be taken
  protected int normalCount = 10;

  /**
   * returns true, if the total state of transition are counted more than the maxNumber for the transition with the highest priority
   */
  protected boolean countTransition() {
    boolean currentAborted = aborted;

    for (Pair<BoolExpr, String> transitionPair : currentTransitions) {

      String transition = transitionPair.getRight();

      // get number of visits for the current transition considering all transitions visited in the dse run
      Pair<String, Integer> duplicate1 = compareTransitions(transitionsAll, transition);

      // get number of visits for the current transition considering all transition visited in the current computation
      // of the component
      Pair<String, Integer> duplicate2 = compareTransitions(currentCountedTransitions, transition);

      // get maxNum according to the classification of the transition
      int maxNum = getMaxNum(transition);

      // case: the transition was already visited in the current computation and the whole dse run
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
          currentCountedTransitions.remove(duplicate2);
          currentCountedTransitions.add(MutablePair.of(duplicate2.getLeft(), duplicate2.getRight() + 1));
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
          currentCountedTransitions.remove(duplicate2);
          currentCountedTransitions.add(MutablePair.of(duplicate2.getLeft(), duplicate2.getRight() + 1));
        }
      } else {
        currentCountedTransitions.add(MutablePair.of(transition, 1));
      }

    }
    currentTransitions.clear();
    return false;

  }

  @Override
  public boolean shouldEndRun() {

    List<Integer> maxNums = new ArrayList<>();

    // get classification of each transition
    for (Pair<BoolExpr, String> transitionPair : currentTransitions) {
      maxNums.add(getMaxNum(transitionPair.getRight()));
    }

    // transitions classified as interesting are labeled with -1
    if (maxNums.contains(-1)) {
      countTransition();
      currentTransitions.clear();
      aborted = false;
      return false;
    } else {
      boolean result = countTransition();
      currentTransitions.clear();
      return result;
    }

  }

  /**
   * function to calculate the maximal number of visits for a transition based on their classification
   */
  protected int getMaxNum(String transition) {
    if (boring.contains(transition)) {
      return boringCount;
    }
    if (interesting.contains(transition)) {
      return -1;
    }
    return normalCount;
  }
}
