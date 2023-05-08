/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.dse.strategie;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import montiarc.rte.dse.TestController;
import montiarc.rte.dse.TestControllerI;
import montiarc.rte.log.LogException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PathCoverageController<In, Out> implements TestControllerI {
  protected final Function<In, Out> sut;
  protected final Context ctx;
  protected Function<Model, In> evalModel;
  protected List<BoolExpr> branchingConditions = new ArrayList<>();
  protected int usedOracleCount = 0;
  protected List<Boolean> oracles = new ArrayList<>();

  protected PathCoverageController(Function<In, Out> sut) {
    if (sut == null) {
      throw new IllegalArgumentException("passed function for PathCoverageController is null");
    }
    this.sut = sut;

    Map<String, String> cfg = new HashMap<>();
    cfg.put("model", "true");

    ctx = new Context(cfg);
  }

  public static <In, Out> PathCoverageController<In, Out> init(Function<In, Out> sut) throws Exception {
    PathCoverageController<In, Out> result = new PathCoverageController<>(sut);
    TestController.init(result);
    return result;
  }

  public Set<Pair<In, Out>> startTest(In initialInput, Function<Model, In> evalModel) {
    this.evalModel = evalModel;
    return startTest(initialInput, new ArrayList<>(), 0);
  }

  public Set<Pair<In, Out>> startTest(In input, List<Boolean> oracles, int branchDepth) {
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

    Set<Pair<In, Out>> result = new HashSet<>();

    Out output = sut.apply(input);
    result.add(ImmutablePair.of(input, output));

    montiarc.rte.log.Log.trace("branchingC: " + branchingConditions);

    List<BoolExpr> branches = branchingConditions;  // Lokal speichern, weil das in der Rekursion gelöscht wird

    for (int i = branchDepth; i < branches.size(); i++) {
      Solver s = ctx.mkSolver();

      for (int j = 0; j < i; j++) {
        s.add(branches.get(j));
      }

      // NOT!!
      s.add(ctx.mkNot(branches.get(i)));

      Status status = s.check();
      montiarc.rte.log.Log.trace(status + "\tRun check with: " + Arrays.toString(((Solver) s).getAssertions()));

      if (status == Status.SATISFIABLE) {
        // Load Input Oracles for next run!
        List<Boolean> nextOracles = loadBoolListValue(s.getModel(), getOracleExpressions());

        resetBranchesLog();
        usedOracleCount = 0;

        montiarc.rte.log.Log.trace("model:" + s.getModel());

        result.addAll(startTest(evalModel.apply(s.getModel()), nextOracles, i + 1));
      }
    }
    return result;
  }

  protected void resetBranchesLog() {
    branchingConditions = new ArrayList<>();
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
}
