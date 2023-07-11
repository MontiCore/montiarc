/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.dse.strategies;

import com.microsoft.z3.BoolExpr;
import montiarc.rte.dse.PathCondition;

import java.util.List;

/**
 * MockPathCoverageController for testing purposes, the oracle of this controller will always
 * decide true.
 */
public class MockPathCoverageController<In, Out> extends PathCoverageController<In, Out> {

  public List<BoolExpr> branchingCondition;

  public PathCondition takenBranches;

  @Override
  public void init(){
    super.init();
    takenBranches = new PathCondition();
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

  @Override
  public void addBranch(BoolExpr condition, String branchID) {
    takenBranches.addBranch(condition, branchID);
  }
}

