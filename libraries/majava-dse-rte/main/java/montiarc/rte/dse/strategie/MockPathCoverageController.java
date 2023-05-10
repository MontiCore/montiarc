/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.dse.strategie;

import com.microsoft.z3.BoolExpr;
import montiarc.rte.dse.TestController;

import java.util.List;
import java.util.function.Function;

/**
 * MockPathCoverageController for testing purposes, the oracle of this controller will always
 * decide true.
 */
public class MockPathCoverageController<In, Out> extends PathCoverageController<In, Out> {

  public List<BoolExpr> branchingCondition;

  protected MockPathCoverageController(Function<In, Out> sut) {
    super(sut);
  }

  public static <In, Out> MockPathCoverageController<In, Out> init(Function<In, Out> sut) throws Exception {
    MockPathCoverageController<In, Out> result = new MockPathCoverageController<>(sut);
    TestController.init(result);
    return result;
  }

  @Override
  public boolean getIfOracle(String branchId) {
    BoolExpr booleExpr = ctx.mkBoolConst("oracle_" + usedOracleCount);

    // always true for testing purposes
    boolean result = true;

    return getIf(ctx.mkEq(booleExpr, ctx.mkBool(result)), result, branchId);
  }

  @Override
  public boolean getIf(BoolExpr condition, boolean result, String branchID) {
    branchingConditions.add(ctx.mkEq(condition, ctx.mkBool(result)));
    montiarc.rte.log.Log.trace("BranchingCondition:   " + ctx.mkEq(condition, ctx.mkBool(result)));
    branchingCondition = branchingConditions;
    return result;
  }
}
