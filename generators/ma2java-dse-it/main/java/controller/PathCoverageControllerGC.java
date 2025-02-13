/* (c) https://github.com/MontiCore/monticore */
package controller;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Params;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import montiarc.rte.dse.PathCondition;
import montiarc.rte.dse.ResultI;
import montiarc.rte.dse.TestController;
import montiarc.rte.dse.strategies.PathCoverageController;
import montiarc.rte.dse.strategies.ResultPathController;
import montiarc.rte.log.LogException;

import java.util.Arrays;
import java.util.List;

/**
 * This controller extend the PathCoverageController and implements the same algorithm as PathCoverageController,
 * with one difference. fter each recursion the garbage collector is triggered, to achieve better performance
 */
public class PathCoverageControllerGC<In, Out> extends PathCoverageController<In, Out> {

  @Override
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
    System.gc();
    result.addInputsAndCondition(input, output, takenBranches);

    montiarc.rte.log.Log.trace("branchingC: " + branchingConditions);

    List<BoolExpr> branches = branchingConditions;

    for (int i = branchDepth; i < branches.size(); i++) {
      Solver s = ctx.mkSolver();

      // add timeout to optimize speed of solver
      Params p = ctx.mkParams();
      p.add("timeout", 10);
      s.setParameters(p);

      for (int j = 0; j < i; j++) {
        s.add(branches.get(j));
      }

      // NOT!!
      s.add(ctx.mkNot(branches.get(i)));

      Status status = s.check();
      solverCalls++;
      montiarc.rte.log.Log.trace(status + "\tRun check with: "
              + Arrays.toString(s.getAssertions()));

      if (status == Status.SATISFIABLE) {
        satPaths++;
        // Load Input Oracles for next run!
        List<Boolean> nextOracles = loadBoolListValue(s.getModel(), getOracleExpressions());

        resetBranchesLog();
        usedOracleCount = 0;

        montiarc.rte.log.Log.trace("model:" + s.getModel());

        result.addAll(startTest(evalModel.apply(s.getModel()), nextOracles, i + 1));
        System.gc();
      }
    }
    return result;
  }
}
