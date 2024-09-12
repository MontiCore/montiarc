/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton.guards;

import montiarc.rte.automaton.Guard;

public interface BooleanGuard extends Guard<Boolean> {

  @Override
  default boolean check(Boolean msg) {
    return realCheck(msg);
  }

  boolean realCheck(boolean msg);
}
