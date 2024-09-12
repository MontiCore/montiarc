/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton.guards;

import montiarc.rte.automaton.Guard;

public interface LongGuard extends Guard<Long> {

  @Override
  default boolean check(Long msg) {
    return realCheck(msg);
  }

  boolean realCheck(long msg);
}
