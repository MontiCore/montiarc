/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton.actions;

import montiarc.rte.automaton.Action;

public interface ByteAction extends Action<Byte> {

  @Override
  default void execute(Byte msg) {
    realExecute(msg);
  }

  void realExecute(byte msg);
}
