/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton.guards;

import montiarc.rte.automaton.Guard;

public interface ByteGuard extends Guard<Byte> {

  @Override
  default boolean check(Byte msg) {
    return realCheck(msg);
  }

  boolean realCheck(byte msg);
}
