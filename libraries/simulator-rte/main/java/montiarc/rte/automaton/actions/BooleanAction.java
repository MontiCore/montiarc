/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton.actions;

import montiarc.rte.automaton.Action;

public interface BooleanAction extends Action<Boolean> {

  @Override
  default void execute(Boolean msg) {
    realExecute(msg);
  }

  void realExecute(boolean msg);
}
