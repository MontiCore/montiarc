/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton.guards;

import montiarc.rte.automaton.Guard;

public interface CharGuard extends Guard<Character> {

  @Override
  default boolean check(Character msg) {
    return realCheck(msg);
  }

  boolean realCheck(char msg);
}
