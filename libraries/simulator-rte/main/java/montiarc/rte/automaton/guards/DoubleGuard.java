/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.automaton.guards;

import montiarc.rte.automaton.Guard;

public interface DoubleGuard extends Guard<Double> {

  @Override
  default boolean check(Double msg) {
    return realCheck(msg);
  }

  boolean realCheck(double msg);
}
