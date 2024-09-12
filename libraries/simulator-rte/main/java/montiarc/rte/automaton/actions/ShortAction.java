/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton.actions;

import montiarc.rte.automaton.Action;

public interface ShortAction extends Action<Short> {

  @Override
  default void execute(Short msg) {
    realExecute(msg);
  }

  void realExecute(short msg);
}
