/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton.guards;

import montiarc.rte.automaton.Guard;

public interface FloatGuard extends Guard<Float> {

  @Override
  default boolean check(Float msg) {
    return realCheck(msg);
  }

  boolean realCheck(float msg);
}
