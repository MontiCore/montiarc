/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.dse;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public interface TransitionSelectorI {
  /**
   * Gets a list of possible Transitions and runs ONE.
   * If list is empty, nothing is happening.
   *
   * @param possibleTransitions:
   */
  void selectTransition(List<Pair<Runnable, String>> possibleTransitions);
}
