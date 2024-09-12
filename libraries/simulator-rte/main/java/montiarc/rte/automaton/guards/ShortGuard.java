/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton.guards;

import montiarc.rte.automaton.Guard;

public interface ShortGuard extends Guard<Short> {

  @Override
  default boolean check(Short msg) {
    return realCheck(msg);
  }

  boolean realCheck(short msg);
}
