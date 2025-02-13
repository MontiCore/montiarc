/* (c) https://github.com/MontiCore/monticore */
package controller;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import montiarc.rte.dse.*;
import montiarc.rte.dse.strategies.ResultPathController;
import montiarc.rte.log.LogException;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * This controller does not use a SMTSolver to generate new input values.
 * New input values a generated randomly.
 * The controller is not usable without concrete implementation of randomInput() for a specific
 * component.
 * This function can not be set with the initialization, because the initialization is inside the
 * dse-tool and not usable for the user
 */
public class IgnoreSMTSolverController<In, Out> extends AbstractController<In, Out> implements EvaluationControllerI {

  protected Function<In, Out> sut;
  protected Function<Model, In> evalModel;
  protected List<BoolExpr> branchingConditions = new ArrayList<>();

  protected int inputLength = 0;

  /**
   * defines the maximal recursive depth of this controller.
   * This results in the number of paths the controller can explore
   */
  protected final Integer recursionDepth;

  // for evaluation purpose, to track the number of solver calls
  protected int solverCalls = 0;

  // for evaluation purpose, to track the number of satisfiability paths
  protected int satPaths = 0;

  public IgnoreSMTSolverController(Integer recursionDepth) {
    this.recursionDepth = recursionDepth;
  }

  /**
   * the default recursiveDepth of the controller is 10
   */
  public IgnoreSMTSolverController() {
    this.recursionDepth = 10;
  }

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

  public ResultI<In, Out> startTest(In input, List<Boolean> oracles, int branchDepth) {
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

    Solver s = ctx.mkSolver();

    List<Boolean> nextOracles = loadBoolListValue(s.getModel(), getOracleExpressions());

    Pair<List<ListerI>, ListerI> nextInput = randomInput();

    if (branchDepth < recursionDepth) {

      resetBranchesLog();
      usedOracleCount = 0;
      result.addAll(startTest((In) nextInput, nextOracles, branchDepth + 1));
      System.gc();
    }
    return result;
  }

  /**
   * calculates the new input values, needs to be overridden in the subclass
   */
  protected Pair<List<ListerI>, ListerI> randomInput() {
    return null;
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
