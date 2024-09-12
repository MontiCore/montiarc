/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton.actions;

import montiarc.rte.automaton.Action;

public interface CharAction extends Action<Character> {

  @Override
  default void execute(Character msg) {
    realExecute(msg);
  }

  void realExecute(char msg);
}
