/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton.actions;

import montiarc.rte.automaton.Action;

public interface LongAction extends Action<Long> {

  @Override
  default void execute(Long msg) {
    realExecute(msg);
  }

  void realExecute(long msg);
}
