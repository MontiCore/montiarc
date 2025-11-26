/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.dse;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Model;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class implements all common functions of a controller, in order to provide a template for all controllers
 */
public abstract class AbstractController<In, Out> implements ControllerI<In, Out> {

  protected int usedOracleCount = 0;
  protected List<Boolean> oracles = new ArrayList<>();
  protected Context ctx;
  protected List<BoolExpr> branchingConditions = new ArrayList<>();
  protected PathCondition takenBranches;

  /**
   * This variable stores all visited states. Since a model to be examined can be a composition
   * of several models, the overall state must be stored. The total state consists of lists of
   * StateInfos, each of which reflects the state of the individual components.
   */
  protected Set<StatesList> visitedStates = new LinkedHashSet<>();

  @Override
  public void init() {
    Map<String, String> cfg = new LinkedHashMap<>();
    cfg.put("model", "true");

    ctx = new Context(cfg);
    TestController.init(this);
  }

  @Override
  public boolean getIfOracle(String branchId) {
    BoolExpr booleExpr = this.ctx.mkBoolConst("oracle_" + usedOracleCount);

    boolean result = true;
    if (this.usedOracleCount < this.oracles.size()) {
      result = this.oracles.get(this.usedOracleCount);
    }

    this.usedOracleCount++;
    return getIf(this.ctx.mkEq(booleExpr, this.ctx.mkBool(result)), result, branchId);
  }

  @Override
  public boolean getIf(BoolExpr condition, boolean result, String branchID) {
    branchingConditions.add(ctx.mkEq(condition, ctx.mkBool(result)));
    montiarc.rte.log.Log.trace("BranchingCondition:   " + ctx.mkEq(condition, ctx.mkBool(result)));
    return result;
  }

  @Override
  public void saveStates(StatesList info) {
    visitedStates.add(info);
  }

  @Override
  public void addBranch(BoolExpr condition, String branchId) {
    takenBranches.addBranch(condition, branchId);
  }

  /**
   * resets the branching conditions
   */
  protected void resetBranchesLog() {
    branchingConditions = new ArrayList<>();
  }

  protected Boolean loadBoolValue(Model model, BoolExpr boolExpr) {
    return model.eval(boolExpr, true).isTrue();
  }

  protected List<Boolean> loadBoolListValue(Model model, List<BoolExpr> boolExpr) {
    return boolExpr.stream().map(b -> loadBoolValue(model, b)).collect(Collectors.toList());
  }

  @Override
  public boolean shouldEndRun() {
    return false;
  }

  @Override
  public Context getCtx() {
    return ctx;
  }

  public List<BoolExpr> getBranchingConditions() {
    return this.branchingConditions;
  }

  /**
   * returns the expressions of the oracles used
   */
  protected List<BoolExpr> getOracleExpressions() {
    List<BoolExpr> result = new ArrayList<>(usedOracleCount);
    for (int i = 0; i < usedOracleCount; i++) {
      result.add(ctx.mkBoolConst("oracle_" + usedOracleCount));
    }
    return result;
  }

}
