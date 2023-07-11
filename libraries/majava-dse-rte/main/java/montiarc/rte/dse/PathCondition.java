/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.dse;

import com.microsoft.z3.BoolExpr;

import java.util.ArrayList;
import java.util.List;

public class PathCondition{

  private BoolExpr branchConditions = TestController.getCtx().mkBool(true);

  private List<String> branchIds = new ArrayList<>();

  public void addBranch(BoolExpr expr, String branchId){
    branchConditions = TestController.getCtx().mkAnd(branchConditions, expr);
    branchIds.add(branchId);
  }

  public BoolExpr getBranchConditions() {
    return this.branchConditions;
  }

  public List<String> getBranchIds() {
    return this.branchIds;
  }


  @Override
  public String toString() {
    return "[<" + branchConditions.toString() + ">, " + String.join(",", branchIds) + "]";
  }
}