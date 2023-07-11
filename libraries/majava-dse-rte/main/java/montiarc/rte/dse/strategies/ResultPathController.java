/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.dse.strategies;

import montiarc.rte.dse.InputAndCondition;
import montiarc.rte.dse.PathCondition;
import montiarc.rte.dse.ResultI;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.Set;

public class ResultPathController<In, Out> implements ResultI<In, Out> {

  private Set<Pair<In, Out>> interestingInputs = new HashSet<>();
  private Set<InputAndCondition> inputsAndConditions = new HashSet<>();

  @Override
  public Set<Pair<In, Out>> getInterestingInputs() {
    return interestingInputs;
  }

  @Override
  public Set<InputAndCondition> getInputsAndCondition() {
    return inputsAndConditions;
  }

  public void addInterestingInputs(In input, Out output) {
    interestingInputs.add(ImmutablePair.of(input, output));
  }

  public void addAll(ResultI<In, Out> startTest) {
    for (Pair<In, Out> pair : startTest.getInterestingInputs()) {
      interestingInputs.add(pair);
    }
    for (InputAndCondition pair : startTest.getInputsAndCondition()) {
      inputsAndConditions.add(pair);
    }
  }

  public void addInputsAndCondition(In input, Out output, PathCondition pathCondition) {
    inputsAndConditions.add(InputAndCondition.newInputCondition(input, output, pathCondition));
  }
}
