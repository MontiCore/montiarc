/* (c) https://github.com/MontiCore/monticore */
package controller;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * This controller extend the PathCoverageController and implements a different oracle
 */
public class PathCoverageOracleController<In, Out> extends PathCoverageControllerGC<In, Out> {

  protected List<Boolean> runOracles = new ArrayList<>();

  @Override
  public List<Boolean> loadBoolListValue(Model model, List<BoolExpr> boolExpr) {
    List<Boolean> result = new ArrayList<>();

    for (boolean expr : runOracles) {
      result.add(!expr);
    }
    runOracles.clear();
    return result;
  }

  @Override
  public boolean getIfOracle(String branchId) {
    BoolExpr booleExpr = ctx.mkBoolConst("oracle_" + usedOracleCount);

    boolean result = true;
    if (usedOracleCount < oracles.size()) {
      result = oracles.get(usedOracleCount);
    }

    runOracles.add(result);
    usedOracleCount++;
    return getIf(ctx.mkEq(booleExpr, ctx.mkBool(result)), result, branchId);
  }

  public List<Boolean> getRunOracles() {
    return runOracles;
  }
}
