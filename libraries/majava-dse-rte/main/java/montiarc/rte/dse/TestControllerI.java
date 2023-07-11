/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.dse;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;

import java.util.List;
import java.util.Set;

public interface TestControllerI extends TransitionSelectorI {

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
   * This function adds a condition equal to the result of its check to the path constraint of
   * the controller. The result of the check is returned to allow the function to store a
   * condition directly when it is checked. The purpose of this function is to store all
   * recognized conditions, regardless of whether the corresponding branch was taken or not.
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

  /**
   * Adds the given condition to the controller's takenBranches to track the path through the
   * component. Unlike getIf, this function is only there to store the path conditions of the
   * taken branches, this is only needed for tracking and evaluating the results.
   *
   * @param condition branch condition as boolean expression
   * @param branchID  taken branch for tracking
   */
  void addBranch(BoolExpr condition, String branchID);

  /**
   * Adds the given StatesList to the controllers visited States
   */
  void saveStates(StatesList info);
}
