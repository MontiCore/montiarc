/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.dse;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;

public interface TestControllerI {

  /**
   * Provides the ability to implement abort strategies for runs
   *
   * @return boolean if the current run should be ended
   */
  boolean shouldEndRun();

  /**
   * An oracle decides whether to take the current branch or not.
   *
   * @param branchId
   * @return A boolean value that indicates whether the branch should be taken or not.
   */
  boolean getIfOracle(String branchId);

  /**
   * This function adds a condition, equal to the result of its check, to the controller's path
   * constraint. The result of the check is returned to allow the functionality to store a
   * condition directly when it is checked.
   *
   * @param condition Branch condition as boolean expression
   * @param result    Result of comparing the branch condition with the actual value
   * @param branchID  Current branch for tracking
   * @return the result
   */
  boolean getIf(BoolExpr condition, boolean result, String branchID);

  /**
   * @return the context of the current TestController
   */
  Context getCtx();
}
