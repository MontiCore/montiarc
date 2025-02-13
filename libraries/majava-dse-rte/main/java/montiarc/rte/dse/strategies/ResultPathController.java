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

  private Set<InputAndCondition<In, Out>> inputsAndConditions = new HashSet<>();

  @Override
  public Set<Pair<In, Out>> getInterestingInputs() {

    Set<Pair<In,Out>> interestingInputs = new HashSet<>();
    for(InputAndCondition<In,Out> condition : inputsAndConditions){
      interestingInputs.add(ImmutablePair.of(condition.getInput(), condition.getOutput()));
    }

    return interestingInputs;
  }

  @Override
  public Set<InputAndCondition<In, Out>> getInputsAndCondition() {
    return inputsAndConditions;
  }

  public void addAll(ResultI<In, Out> startTest) {
    inputsAndConditions.addAll(startTest.getInputsAndCondition());
  }

  public void addInputsAndCondition(In input, Out output, PathCondition pathCondition) {
    inputsAndConditions.add(InputAndCondition.newInputCondition(input, output, pathCondition));
  }
}