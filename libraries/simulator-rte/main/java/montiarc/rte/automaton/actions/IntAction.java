/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton.actions;

import montiarc.rte.automaton.Action;

public interface IntAction extends Action<Integer> {

  @Override
  default void execute(Integer msg) {
    realExecute(msg);
  }

  void realExecute(int msg);
}
