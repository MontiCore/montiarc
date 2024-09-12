/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton.actions;

import montiarc.rte.automaton.Action;

public interface FloatAction extends Action<Float> {

  @Override
  default void execute(Float msg) {
    realExecute(msg);
  }

  void realExecute(float msg);
}
