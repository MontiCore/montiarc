/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.dse;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Set;

public interface ResultI<In, Out> {

  /**
   * this function returns all collected input output pairs
   */
  Set<Pair<In, Out>> getInterestingInputs();

  /**
   * this function returns all collected input output pairs with the corresponding taken branch
   * condition
   */
  Set<InputAndCondition> getInputsAndCondition();

}