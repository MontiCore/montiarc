/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.dse;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

import static montiarc.rte.dse.TestController.getIfOracle;

public interface TransitionSelectorI {
  /**
   * Gets a list of possible Transitions and runs ONE.
   * If list is empty, nothing is happening.
   *
   * @param possibleTransitions:
   */
  default void selectTransition(List<Pair<Runnable, String>> possibleTransitions) {
    if (possibleTransitions.size() == 1) {
      possibleTransitions.get(0).getKey().run();
    }

    if (possibleTransitions.size() > 1) {
      int transition = 0;
      while (transition < possibleTransitions.size() - 1 && !getIfOracle("possibleTransitions")) {
        transition++;
      }
      possibleTransitions.get(transition).getKey().run();
    }
  }
}
