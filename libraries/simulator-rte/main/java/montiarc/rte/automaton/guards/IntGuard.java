/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton.guards;

import montiarc.rte.automaton.Guard;

public interface IntGuard extends Guard<Integer> {

  @Override
  default boolean check(Integer msg) {
    return realCheck(msg);
  }

  boolean realCheck(int msg);
}
