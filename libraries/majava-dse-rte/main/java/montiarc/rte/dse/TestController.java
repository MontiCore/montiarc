/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.dse;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;

public class TestController {
  protected static TestControllerI controller;

  public static void init(TestControllerI controller) {
    TestController.controller = controller;
  }

  public static TestControllerI getController() {
    return TestController.controller;
  }

  public static boolean shouldEndRun() {
    return controller.shouldEndRun();
  }

  public static boolean getIfOracle(String branchId) {
    return controller.getIfOracle(branchId);
  }

  public static boolean getIf(BoolExpr condition, boolean result, String branchID) {
    return controller.getIf(condition, result, branchID);
  }

  public static Context getCtx() {
    return controller.getCtx();
  }
}
