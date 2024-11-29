/* (c) https://github.com/MontiCore/monticore */
package controller;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import montiarc.rte.dse.ControllerI;
import montiarc.rte.dse.EvaluationControllerI;
import montiarc.rte.dse.ListerI;
import montiarc.rte.dse.PathCondition;
import montiarc.rte.dse.ResultI;
import montiarc.rte.dse.StatesList;
import montiarc.rte.dse.TestController;
import montiarc.rte.dse.strategies.ResultPathController;
import montiarc.rte.log.LogException;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This controller does not use a SMTSolver to generate new input values.
 * New input values a generated randomly.
 * The controller is not usable without concrete implementation of randomInput() for a specific
 * component.
 * This function can not be set with the initialization, because the initialization is inside the
 * dse-tool and not usable for the user
 */
public class IgnoreSMTSolverController<In, Out> implements ControllerI<In, Out>, EvaluationControllerI {

  protected Function<In, Out> sut;
  protected Context ctx;
  protected Function<Model, In> evalModel;
  protected List<BoolExpr> branchingConditions = new ArrayList<>();
  protected int usedOracleCount = 0;
  protected List<Boolean> oracles = new ArrayList<>();
  protected PathCondition takenBranches;
  protected Set<StatesList> visitedStates = new HashSet<>();

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
  public void init() {
    Map<String, String> cfg = new HashMap<>();
    cfg.put("model", "true");

    ctx = new Context(cfg);
    TestController.init(this);
  }

  @Override
  public ResultI<In, Out> startTest(In initialInput,
                                    Function<Model, In> evalModel,
                                    Function<In, Out> sut) {
    if (sut == null) {
      throw new IllegalArgumentException("passed function for PathCoverageController is null");
    }

    this.sut = sut;
    this.evalModel = evalModel;
    return startTest(initialInput, new ArrayList<>(), 0);
  }

  @Override
  public Context getCtx() {
    return ctx;
  }

  @Override
  public boolean shouldEndRun() {
    return false;
  }

  @Override
  public boolean getIfOracle(String branchId) {
    BoolExpr booleExpr = ctx.mkBoolConst("oracle_" + usedOracleCount);

    boolean result = true;
    if (usedOracleCount < oracles.size()) {
      result = oracles.get(usedOracleCount);
    }

    usedOracleCount++;
    return getIf(ctx.mkEq(booleExpr, ctx.mkBool(result)), result, branchId);
  }

  protected List<BoolExpr> getOracleExpressions() {
    List<BoolExpr> result = new ArrayList<>(usedOracleCount);
    for (int i = 0; i < usedOracleCount; i++) {
      result.add(ctx.mkBoolConst("oracle_" + usedOracleCount));
    }
    return result;
  }

  protected Boolean loadBoolValue(Model model, BoolExpr boolExpr) {
    return model.eval(boolExpr, true).isTrue();
  }

  protected List<Boolean> loadBoolListValue(Model model, List<BoolExpr> boolExpr) {
    return boolExpr.stream().map(b -> loadBoolValue(model, b)).collect(Collectors.toList());
  }

  @Override
  public boolean getIf(BoolExpr condition, boolean result, String branchID) {
    branchingConditions.add(ctx.mkEq(condition, ctx.mkBool(result)));
    montiarc.rte.log.Log.trace("BranchingCondition:   " + ctx.mkEq(condition, ctx.mkBool(result)));
    return result;
  }

  @Override
  public void addBranch(BoolExpr condition, String branchId) {
    takenBranches.addBranch(condition, branchId);
  }

  @Override
  public void selectTransition(List<Pair<Runnable, String>> possibleTransitions) {
    if (possibleTransitions.size() == 1) {
      possibleTransitions.get(0).getKey().run();
    }

    if (possibleTransitions.size() > 1) {
      int transition = 0;
      while (transition < possibleTransitions.size() - 1 && !getIfOracle("possibleTransitions")) {
        transition++;
      }
      possibleTransitions.get(transition).getKey().run();
    }
  }

  @Override
  public void saveStates(StatesList info) {
    visitedStates.add(info);
  }

  @Override
  public Set<StatesList> getVisitedStates() {
    return visitedStates;
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
    Status status = s.check();

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

  protected void resetBranchesLog() {
    branchingConditions = new ArrayList<>();
  }

  @Override
  public int getSolverCalls() {
    return solverCalls;
  }

  @Override
  public int getSatPaths() {
    return satPaths;
  }
}
