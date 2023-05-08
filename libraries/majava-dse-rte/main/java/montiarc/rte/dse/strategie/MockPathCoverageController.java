/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.dse.strategie;

import com.microsoft.z3.*;

import java.util.function.Function;

/**
 * MockPathCoverageController for testing purposes, the oracle of this controller will always decide true.
 */
public class MockPathCoverageController<In, Out> extends PathCoverageController<In, Out> {

  protected MockPathCoverageController(Function<In, Out> sut) {
    super(sut);
  }

  @Override
  public boolean getIfOracle(String branchId) {
    BoolExpr booleExpr = ctx.mkBoolConst("oracle_" + usedOracleCount);

    // always true for testing purposes
    boolean result = true;

    return getIf(ctx.mkEq(booleExpr, ctx.mkBool(result)), result, branchId);
  }
}
