/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton.actions;

import montiarc.rte.automaton.Action;

public interface DoubleAction extends Action<Double> {

  @Override
  default void execute(Double msg) {
    realExecute(msg);
  }

  void realExecute(double msg);
}
