/* (c) https://github.com/MontiCore/monticore */
package controller;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Model;
import montiarc.rte.dse.*;
import montiarc.rte.dse.strategies.ResultPathController;
import montiarc.rte.log.LogException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * This controller runs exactly once with the initial input
 */
public class RunOnceController<In, Out> extends AbstractController<In, Out> implements EvaluationControllerI {

  protected Function<In, Out> sut;

  protected Function<Model, In> evalModel;
  protected List<BoolExpr> branchingConditions = new ArrayList<>();

  // for evaluation purpose, to track the number of solver calls
  protected int solverCalls = 0;

  // for evaluation purpose, to track the number of satisfiability paths
  protected int satPaths = 0;

  @Override
  public ResultI<In, Out> startTest(In initialInput,
                                    Function<Model, In> evalModel,
                                    Function<In, Out> sut) {
    if (sut == null) {
      throw new IllegalArgumentException("passed function for the controller is null");
    }

    this.sut = sut;
    this.evalModel = evalModel;
    return startTest(initialInput, new ArrayList<>(), 0);
  }

  private ResultI<In, Out> startTest(In input, List<Boolean> oracles, int branchDepth) {
    if (TestController.getController() != this) {
      throw new LogException("Given controller does not match the " +
              "PathCoverageController");
    }
    if (!branchingConditions.isEmpty()) {
      throw new LogException("BranchingCondition is not empty, although it should " +
              "be, because a new path was started");
    }
    if (usedOracleCount != 0) {
      throw new LogException("usedOracleCount is not zero, although it should be, " +
              "because a new path was started");
    }
    this.oracles = oracles;

    takenBranches = new PathCondition();

    ResultPathController<In, Out> result = new ResultPathController<>();

    Out output = sut.apply(input);
    result.addInputsAndCondition(input, output, takenBranches);

    return result;
  }

  @Override
  public int getSolverCalls() {
    return solverCalls;
  }

  @Override
  public int getSatPaths() {
    return satPaths;
  }

  @Override
  public Set<StatesList> getVisitedStates() {
    return visitedStates;
  }
}
