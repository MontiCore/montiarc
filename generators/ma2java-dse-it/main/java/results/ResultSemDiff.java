/* (c) https://github.com/MontiCore/monticore */
package results;

import montiarc.rte.dse.InputAndCondition;
import montiarc.rte.dse.ResultI;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.Set;

/**
 * This class defines the type of the result for the computation of semDiff
 */
public class ResultSemDiff<In, Out> implements ResultI<In, Out> {

  private Set<Pair<In, Out>> interestingInputs = new HashSet<>();
  private Set<InputAndCondition<In, Out>> inputsAndConditions = new HashSet<>();

  private Set<Pair<In, Out>> semDiff = new HashSet<>();

  public void addSemDiffPair(Pair<In, Out> semDiff) {
    this.semDiff.add(semDiff);
  }

  public Set<Pair<In, Out>> getSemDiff() {
    return semDiff;
  }

  @Override
  public Set<Pair<In, Out>> getInterestingInputs() {
    return null;
  }

  @Override
  public Set<InputAndCondition<In, Out>> getInputsAndCondition() {
    return null;
  }

}
